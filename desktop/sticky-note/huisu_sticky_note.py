from __future__ import annotations

import base64
import copy
import io
import json
import mimetypes
import os
import sys
import threading
import time
import tkinter as tk
import urllib.error
import urllib.parse
import urllib.request
import uuid
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from tkinter import filedialog, messagebox, ttk
from typing import Any, Callable

from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from PIL import Image, ImageDraw, ImageGrab, ImageTk
import pystray


DEFAULT_SERVER_URL = "http://106.53.73.104:18080"
DEFAULT_API_TOKEN = "lee123456"
SNAPSHOT_SCHEMA_VERSION = 1
MAX_IMAGE_BYTES = 5 * 1024 * 1024
WIN10_BG = "#f3f3f3"
WIN10_PANEL = "#ffffff"
WIN10_INPUT = "#fbfbfb"
WIN10_TEXT = "#1f1f1f"
WIN10_MUTED = "#605e5c"
WIN10_BORDER = "#d0d0d0"
WIN10_ACCENT = "#0078d4"
WIN10_ACCENT_DARK = "#005a9e"
SYNC_TABLES = [
    "meditation_records",
    "affirmation_records",
    "affirmations",
    "video_links",
    "achievements",
    "todo_categories",
    "todo_items",
    "quick_notes",
    "quick_note_images",
]

SPACE_LABELS = {
    "PERSONAL": "个人",
    "WORK": "工作",
    "KEY": "密钥",
}

SPACE_VALUES = {value: key for key, value in SPACE_LABELS.items()}

TYPE_LABELS = {
    "NOTE": "笔记",
    "MEMORY": "记忆",
    "WORK": "工作记录",
    "IDEA": "灵感",
    "TODO": "待办",
    "MATERIAL": "资料",
}

TYPE_VALUES = {value: key for key, value in TYPE_LABELS.items()}


def app_base_dir() -> Path:
    if getattr(sys, "frozen", False):
        return Path(sys.executable).resolve().parent
    return Path(__file__).resolve().parent


BASE_DIR = app_base_dir()
CONFIG_PATH = BASE_DIR / "huisu_sticky_config.json"


def clamp_opacity(value: Any) -> float:
    try:
        opacity = float(value)
    except Exception:
        opacity = 1.0
    return max(0.82, min(1.0, opacity))


@dataclass
class SyncConfig:
    server_url: str = DEFAULT_SERVER_URL
    api_token: str = DEFAULT_API_TOKEN
    device_id: str = ""
    encryption_password: str = ""
    opacity: float = 1.0

    @classmethod
    def load(cls) -> "SyncConfig":
        if CONFIG_PATH.exists():
            try:
                raw = json.loads(CONFIG_PATH.read_text(encoding="utf-8"))
                raw_opacity = raw.get("opacity", 1.0)
                if abs(clamp_opacity(raw_opacity) - 0.94) < 0.001:
                    raw_opacity = 1.0
                config = cls(
                    server_url=raw.get("server_url", DEFAULT_SERVER_URL),
                    api_token=raw.get("api_token", DEFAULT_API_TOKEN),
                    device_id=raw.get("device_id", ""),
                    encryption_password=raw.get("encryption_password", ""),
                    opacity=clamp_opacity(raw_opacity),
                )
            except Exception:
                config = cls()
        else:
            config = cls()

        if not config.device_id:
            config.device_id = f"windows-{uuid.uuid4().hex[:8]}"
        return config

    def save(self) -> None:
        payload = {
            "server_url": self.server_url.strip(),
            "api_token": self.api_token.strip(),
            "device_id": self.device_id.strip(),
            "encryption_password": self.encryption_password,
            "opacity": clamp_opacity(self.opacity),
        }
        CONFIG_PATH.write_text(
            json.dumps(payload, ensure_ascii=False, indent=2),
            encoding="utf-8",
        )

    def validate(self) -> None:
        if not self.server_url.strip() or not self.api_token.strip() or not self.device_id.strip():
            raise ValueError("请先在设置里填写服务地址、Token 和设备 ID。")


class SyncClient:
    def __init__(self, config: SyncConfig):
        self.config = config

    def probe(self) -> dict[str, Any]:
        return self._request("POST", "/v1/sync/probe")

    def get_snapshot(self) -> dict[str, Any]:
        response = self._request("GET", "/v1/sync/snapshot")
        if not response.get("exists") or not response.get("snapshot"):
            return {"revision": 0, "updated_at": "", "snapshot": create_empty_snapshot()}
        return {
            "revision": int(response.get("revision", 0)),
            "updated_at": response.get("updated_at") or "",
            "snapshot": decode_snapshot(response["snapshot"], self.config.encryption_password),
        }

    def put_snapshot(self, snapshot: dict[str, Any], base_revision: int) -> dict[str, Any]:
        payload = {
            "device_id": self.config.device_id.strip(),
            "base_revision": int(base_revision),
            "client_updated_at": str(int(time.time() * 1000)),
            "snapshot": encode_snapshot(snapshot, self.config.encryption_password),
        }
        return self._request("PUT", "/v1/sync/snapshot", payload)

    def _request(self, method: str, path: str, body: dict[str, Any] | None = None) -> dict[str, Any]:
        self.config.validate()
        server_url = self.config.server_url.strip().rstrip("/")
        parsed = urllib.parse.urlsplit(server_url)
        if parsed.scheme not in {"http", "https"} or not parsed.netloc:
            raise ValueError("服务地址格式不正确。")

        data = None
        headers = {
            "Accept": "application/json",
            "Authorization": f"Bearer {self.config.api_token.strip()}",
            "User-Agent": "HuiSu-QuickNote-Sticky/1.0",
        }
        if body is not None:
            data = json.dumps(body, ensure_ascii=False).encode("utf-8")
            headers["Content-Type"] = "application/json; charset=utf-8"

        request = urllib.request.Request(
            f"{server_url}{path}",
            data=data,
            headers=headers,
            method=method,
        )
        try:
            with urllib.request.urlopen(request, timeout=30) as response:
                text = response.read().decode("utf-8")
                return json.loads(text) if text else {}
        except urllib.error.HTTPError as error:
            detail = error.read().decode("utf-8", errors="replace")
            if error.code == 409:
                raise SyncConflictError("云端版本已变化，正在重试。") from error
            raise RuntimeError(f"同步请求失败({error.code}): {detail}") from error
        except urllib.error.URLError as error:
            raise RuntimeError(f"连接同步服务失败: {error.reason}") from error


class SyncConflictError(RuntimeError):
    pass


def create_empty_snapshot() -> dict[str, Any]:
    return {
        "schemaVersion": SNAPSHOT_SCHEMA_VERSION,
        "exportedAt": int(time.time() * 1000),
        "tables": {table: [] for table in SYNC_TABLES},
    }


def normalize_snapshot(snapshot: dict[str, Any]) -> dict[str, Any]:
    if not isinstance(snapshot, dict):
        snapshot = create_empty_snapshot()
    tables = snapshot.setdefault("tables", {})
    for table in SYNC_TABLES:
        if not isinstance(tables.get(table), list):
            tables[table] = []
    snapshot["schemaVersion"] = int(snapshot.get("schemaVersion") or SNAPSHOT_SCHEMA_VERSION)
    snapshot["exportedAt"] = int(snapshot.get("exportedAt") or int(time.time() * 1000))
    return snapshot


def decode_snapshot(snapshot: dict[str, Any], password: str) -> dict[str, Any]:
    if not snapshot.get("encrypted"):
        return normalize_snapshot(snapshot)
    if not password:
        raise ValueError("云端快照已加密，请在设置里填写与手机端一致的快照加密密码。")
    plain_text = decrypt_snapshot(snapshot, password)
    return normalize_snapshot(json.loads(plain_text))


def encode_snapshot(snapshot: dict[str, Any], password: str) -> dict[str, Any]:
    if not password:
        return snapshot
    return encrypt_snapshot(json.dumps(snapshot, ensure_ascii=False, separators=(",", ":")), password)


def encrypt_snapshot(plain_text: str, password: str) -> dict[str, Any]:
    salt = os.urandom(16)
    iv = os.urandom(12)
    key = derive_key(password, salt)
    ciphertext = AESGCM(key).encrypt(iv, plain_text.encode("utf-8"), None)
    return {
        "encrypted": True,
        "algorithm": "AES-256-GCM",
        "kdf": "PBKDF2WithHmacSHA256",
        "iterations": 120000,
        "salt": base64.b64encode(salt).decode("ascii"),
        "iv": base64.b64encode(iv).decode("ascii"),
        "ciphertext": base64.b64encode(ciphertext).decode("ascii"),
    }


def decrypt_snapshot(wrapper: dict[str, Any], password: str) -> str:
    salt = base64.b64decode(wrapper["salt"])
    iv = base64.b64decode(wrapper["iv"])
    ciphertext = base64.b64decode(wrapper["ciphertext"])
    key = derive_key(password, salt)
    plain = AESGCM(key).decrypt(iv, ciphertext, None)
    return plain.decode("utf-8")


def derive_key(password: str, salt: bytes) -> bytes:
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=120000,
    )
    return kdf.derive(password.encode("utf-8"))


def quick_notes(snapshot: dict[str, Any]) -> list[dict[str, Any]]:
    return snapshot["tables"]["quick_notes"]


def quick_note_images(snapshot: dict[str, Any]) -> list[dict[str, Any]]:
    return snapshot["tables"]["quick_note_images"]


def visible_notes(snapshot: dict[str, Any]) -> list[dict[str, Any]]:
    notes = [note for note in quick_notes(snapshot) if note.get("status") != "DELETED"]
    return sorted(
        notes,
        key=lambda note: (to_bool(note.get("isPinned")), int(note.get("updatedAt") or 0)),
        reverse=True,
    )


def to_bool(value: Any) -> bool:
    return value is True or value == 1 or value == "1"


def bool_int(value: bool) -> int:
    return 1 if value else 0


def next_note_id(notes: list[dict[str, Any]]) -> int:
    return max([int(note.get("id") or 0) for note in notes] + [0]) + 1


def next_image_id(images: list[dict[str, Any]]) -> int:
    return max([int(image.get("id") or 0) for image in images] + [0]) + 1


def parse_tags(raw: str) -> list[str]:
    tags: list[str] = []
    for part in raw.replace("，", ",").replace("、", ",").replace("\n", ",").split(","):
        tag = part.strip().lstrip("#").strip()
        if tag and tag not in tags:
            tags.append(tag)
    return tags


def normalize_tags(raw: str) -> str:
    return ", ".join(parse_tags(raw))


def default_title(timestamp_ms: int | None = None) -> str:
    dt = datetime.fromtimestamp((timestamp_ms or int(time.time() * 1000)) / 1000)
    return dt.strftime("%Y-%m-%d %H:%M 记录")


def format_time(timestamp_ms: Any) -> str:
    try:
        dt = datetime.fromtimestamp(int(timestamp_ms) / 1000)
        return dt.strftime("%m-%d %H:%M")
    except Exception:
        return ""


def create_tray_image() -> Image.Image:
    image = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.rounded_rectangle((8, 8, 56, 56), radius=8, fill=(0, 120, 212, 255))
    draw.rectangle((18, 20, 46, 24), fill=(255, 255, 255, 245))
    draw.rectangle((18, 31, 42, 35), fill=(255, 255, 255, 225))
    draw.rectangle((18, 42, 35, 46), fill=(255, 255, 255, 205))
    return image


class StickyNoteApp:
    def __init__(self) -> None:
        self.config = SyncConfig.load()
        self.client = SyncClient(self.config)
        self.snapshot = create_empty_snapshot()
        self.revision = 0
        self.updated_at = ""
        self.selected_note_id: int | None = None
        self.pending_images: list[dict[str, Any]] = []
        self.inline_image_refs: list[ImageTk.PhotoImage] = []
        self.inline_image_widgets: list[tk.Label] = []
        self.busy = False
        self.tray_icon: pystray.Icon | None = None

        self.root = tk.Tk()
        self.root.title("回溯速记")
        self.root.geometry("380x510")
        self.root.minsize(320, 390)
        self.root.configure(bg=WIN10_BG)
        self.root.attributes("-topmost", True)
        self.root.protocol("WM_DELETE_WINDOW", self.hide_window)
        self.root.bind("<F5>", lambda _event: self.run_background(self.sync_from_cloud, "已刷新云端数据"))
        self.apply_opacity()

        self.build_ui()
        self.start_tray()
        self.run_background(self.sync_from_cloud, success_message=None)

    def build_ui(self) -> None:
        self.root.columnconfigure(0, weight=1)
        self.root.rowconfigure(1, weight=1)

        header = tk.Frame(self.root, bg=WIN10_BG)
        header.grid(row=0, column=0, sticky="ew", padx=12, pady=(10, 6))
        header.columnconfigure(0, weight=1)

        self.title_label = tk.Label(
            header,
            text="回溯速记",
            bg=WIN10_BG,
            fg=WIN10_TEXT,
            font=("Microsoft YaHei UI", 15, "bold"),
            anchor="w",
        )
        self.title_label.grid(row=0, column=0, sticky="w")

        tk.Button(header, text="设置", command=self.open_settings, width=6, relief="flat", bg=WIN10_PANEL).grid(row=0, column=1, padx=(6, 0))
        tk.Button(header, text="隐藏", command=self.hide_window, width=6, relief="flat", bg=WIN10_PANEL).grid(row=0, column=2, padx=(6, 0))

        self.status_var = tk.StringVar(value="准备同步")
        status = tk.Label(
            header,
            textvariable=self.status_var,
            bg=WIN10_BG,
            fg=WIN10_MUTED,
            font=("Microsoft YaHei UI", 9),
            anchor="w",
        )
        status.grid(row=1, column=0, columnspan=3, sticky="ew", pady=(4, 0))

        body = tk.Frame(self.root, bg=WIN10_BG)
        body.grid(row=1, column=0, sticky="nsew", padx=12, pady=4)
        body.columnconfigure(0, weight=1)
        body.rowconfigure(1, weight=1)

        self.note_title = ttk.Entry(body)
        self.note_title.grid(row=0, column=0, sticky="ew", pady=(0, 8))
        self.note_title.insert(0, "")

        self.note_content = tk.Text(
            body,
            height=9,
            wrap="word",
            undo=True,
            borderwidth=1,
            relief="solid",
            bg=WIN10_INPUT,
            fg=WIN10_TEXT,
            insertbackground=WIN10_TEXT,
            highlightthickness=1,
            highlightbackground=WIN10_BORDER,
            highlightcolor=WIN10_ACCENT,
            selectbackground=WIN10_ACCENT,
            selectforeground="#ffffff",
            font=("Microsoft YaHei UI", 11),
        )
        self.note_content.grid(row=1, column=0, sticky="nsew")
        self.note_content.bind("<Control-v>", self.paste_into_content)
        self.note_content.bind("<Control-V>", self.paste_into_content)
        self.note_content.bind("<Shift-Insert>", self.paste_into_content)

        meta = tk.Frame(body, bg=WIN10_BG)
        meta.grid(row=2, column=0, sticky="ew", pady=(8, 0))
        meta.columnconfigure(1, weight=1)
        meta.columnconfigure(3, weight=1)

        tk.Label(meta, text="空间", bg=WIN10_BG, fg=WIN10_TEXT).grid(row=0, column=0, sticky="w")
        self.space_var = tk.StringVar(value="个人")
        self.space_box = ttk.Combobox(
            meta,
            textvariable=self.space_var,
            values=list(SPACE_VALUES.keys()),
            state="readonly",
            width=8,
        )
        self.space_box.grid(row=0, column=1, sticky="ew", padx=(5, 10))

        tk.Label(meta, text="类型", bg=WIN10_BG, fg=WIN10_TEXT).grid(row=0, column=2, sticky="w")
        self.type_var = tk.StringVar(value="笔记")
        self.type_box = ttk.Combobox(
            meta,
            textvariable=self.type_var,
            values=list(TYPE_VALUES.keys()),
            state="readonly",
            width=8,
        )
        self.type_box.grid(row=0, column=3, sticky="ew", padx=(5, 0))

        checks = tk.Frame(body, bg=WIN10_BG)
        checks.grid(row=3, column=0, sticky="ew", pady=(8, 0))
        self.favorite_var = tk.BooleanVar(value=False)
        self.pinned_var = tk.BooleanVar(value=False)
        self.topmost_var = tk.BooleanVar(value=True)
        self.image_summary_var = tk.StringVar(value="图片 0")
        tk.Checkbutton(checks, text="收藏", variable=self.favorite_var, bg=WIN10_BG, fg=WIN10_TEXT, activebackground=WIN10_BG).pack(side="left")
        tk.Checkbutton(checks, text="置顶", variable=self.pinned_var, bg=WIN10_BG, fg=WIN10_TEXT, activebackground=WIN10_BG).pack(side="left", padx=(10, 0))
        tk.Checkbutton(
            checks,
            text="窗口置顶",
            variable=self.topmost_var,
            command=self.apply_topmost,
            bg=WIN10_BG,
            fg=WIN10_TEXT,
            activebackground=WIN10_BG,
        ).pack(side="left", padx=(10, 0))
        tk.Button(checks, text="添加图片", command=self.add_images, relief="flat", bg=WIN10_PANEL).pack(side="left", padx=(10, 0))
        tk.Label(checks, textvariable=self.image_summary_var, bg=WIN10_BG, fg=WIN10_MUTED).pack(side="left", padx=(8, 0))

        actions = tk.Frame(body, bg=WIN10_BG)
        actions.grid(row=4, column=0, sticky="ew", pady=(8, 0))
        actions.columnconfigure(0, weight=1)
        actions.columnconfigure(1, weight=1)
        actions.columnconfigure(2, weight=1)
        actions.columnconfigure(3, weight=1)

        self.save_button = tk.Button(actions, text="保存同步", command=self.save_note, bg=WIN10_ACCENT, fg="white", activebackground=WIN10_ACCENT_DARK, activeforeground="white", relief="flat")
        self.save_button.grid(row=0, column=0, sticky="ew", padx=(0, 5))
        self.sync_button = tk.Button(actions, text="刷新", command=lambda: self.run_background(self.sync_from_cloud, "已刷新云端数据"), relief="flat", bg=WIN10_PANEL)
        self.sync_button.grid(row=0, column=1, sticky="ew", padx=5)
        tk.Button(actions, text="新建", command=self.clear_editor, relief="flat", bg=WIN10_PANEL).grid(row=0, column=2, sticky="ew", padx=5)
        self.delete_button = tk.Button(actions, text="删除", command=self.delete_note, fg="#a4262c", relief="flat", bg=WIN10_PANEL)
        self.delete_button.grid(row=0, column=3, sticky="ew", padx=(5, 0))

        recent_frame = tk.Frame(self.root, bg=WIN10_BG)
        recent_frame.grid(row=2, column=0, sticky="ew", padx=12, pady=(4, 10))
        recent_frame.columnconfigure(0, weight=1)
        tk.Label(recent_frame, text="最近速记", bg=WIN10_BG, fg=WIN10_MUTED).grid(row=0, column=0, sticky="w")
        self.note_list = tk.Listbox(
            recent_frame,
            height=5,
            activestyle="none",
            exportselection=False,
            relief="solid",
            borderwidth=1,
            bg=WIN10_PANEL,
            fg=WIN10_TEXT,
            selectbackground=WIN10_ACCENT,
            selectforeground="#ffffff",
        )
        self.note_list.grid(row=1, column=0, sticky="ew", pady=(4, 0))
        self.note_list.bind("<<ListboxSelect>>", self.on_note_selected)

        self.clear_editor()

    def start_tray(self) -> None:
        menu = pystray.Menu(
            pystray.MenuItem("显示", lambda: self.root.after(0, self.show_window), default=True),
            pystray.MenuItem("隐藏", lambda: self.root.after(0, self.hide_window)),
            pystray.MenuItem("刷新", lambda: self.root.after(0, lambda: self.run_background(self.sync_from_cloud, "已刷新云端数据"))),
            pystray.MenuItem("退出", lambda: self.root.after(0, self.exit_app)),
        )
        self.tray_icon = pystray.Icon("HuiSuQuickNote", create_tray_image(), "回溯速记", menu)
        threading.Thread(target=self.tray_icon.run, daemon=True).start()

    def apply_topmost(self) -> None:
        self.root.attributes("-topmost", bool(self.topmost_var.get()))

    def apply_opacity(self) -> None:
        try:
            self.root.attributes("-alpha", clamp_opacity(self.config.opacity))
        except Exception:
            pass

    def show_window(self) -> None:
        self.root.deiconify()
        self.root.lift()
        self.apply_topmost()
        self.note_content.focus_set()

    def hide_window(self) -> None:
        self.root.withdraw()

    def exit_app(self) -> None:
        if self.tray_icon:
            self.tray_icon.stop()
        self.root.destroy()

    def run(self) -> None:
        self.root.mainloop()

    def set_busy(self, busy: bool, message: str | None = None) -> None:
        self.busy = busy
        state = tk.DISABLED if busy else tk.NORMAL
        self.save_button.configure(state=state)
        self.sync_button.configure(state=state)
        self.delete_button.configure(state=state if self.selected_note_id else tk.DISABLED)
        if message:
            self.status_var.set(message)

    def run_background(self, task: Callable[[], None], success_message: str | None = None) -> None:
        if self.busy:
            return

        def worker() -> None:
            try:
                task()
                if success_message:
                    self.root.after(0, lambda: self.status_var.set(success_message))
            except Exception as error:
                self.root.after(0, lambda: self.show_error(str(error)))
            finally:
                self.root.after(0, lambda: self.set_busy(False))

        self.set_busy(True, "同步中...")
        threading.Thread(target=worker, daemon=True).start()

    def show_error(self, message: str) -> None:
        self.status_var.set(message)
        messagebox.showerror("回溯速记", message)

    def sync_from_cloud(self) -> None:
        cloud = self.client.get_snapshot()
        self.snapshot = normalize_snapshot(cloud["snapshot"])
        self.revision = int(cloud["revision"])
        self.updated_at = cloud.get("updated_at", "")
        self.root.after(0, self.refresh_notes)

    def write_with_retry(self, mutator: Callable[[dict[str, Any]], None]) -> None:
        last_error: Exception | None = None
        for attempt in range(2):
            if attempt == 0:
                snapshot = copy.deepcopy(normalize_snapshot(self.snapshot))
                base_revision = int(self.revision)
            else:
                self.root.after(0, lambda: self.status_var.set("云端版本变化，正在重新拉取..."))
                cloud = self.client.get_snapshot()
                snapshot = normalize_snapshot(cloud["snapshot"])
                base_revision = int(cloud["revision"])

            mutator(snapshot)
            snapshot["exportedAt"] = int(time.time() * 1000)
            try:
                response = self.client.put_snapshot(snapshot, base_revision)
                self.snapshot = snapshot
                self.revision = int(response.get("revision", base_revision + 1))
                self.updated_at = response.get("updated_at", "")
                self.root.after(0, self.refresh_notes)
                return
            except SyncConflictError as error:
                last_error = error
                continue
        raise last_error or RuntimeError("云端版本冲突，请拉取后再保存。")

    def save_note(self) -> None:
        if self.busy:
            return
        content = self.note_content.get("1.0", tk.END).strip()
        if not content:
            messagebox.showwarning("回溯速记", "正文不能为空。")
            self.note_content.focus_set()
            return

        selected_id = self.selected_note_id
        title = self.note_title.get().strip()
        note_space = SPACE_VALUES.get(self.space_var.get(), "PERSONAL")
        note_type = TYPE_VALUES.get(self.type_var.get(), "NOTE")
        tags = ""
        favorite = bool_int(self.favorite_var.get())
        pinned = bool_int(self.pinned_var.get())
        now = int(time.time() * 1000)
        saved_id: dict[str, int | None] = {"value": selected_id}

        def mutate(snapshot: dict[str, Any]) -> None:
            notes = quick_notes(snapshot)
            index = -1
            if selected_id:
                for i, note in enumerate(notes):
                    if int(note.get("id") or 0) == int(selected_id):
                        index = i
                        break
            created_at = int(notes[index].get("createdAt") or now) if index >= 0 else now
            note_id = int(notes[index].get("id")) if index >= 0 else next_note_id(notes)
            note = {
                "id": note_id,
                "title": title or default_title(created_at),
                "content": content,
                "type": note_type,
                "space": note_space,
                "tags": tags,
                "isFavorite": favorite,
                "isPinned": pinned,
                "status": "ACTIVE",
                "createdAt": created_at,
                "updatedAt": now,
            }
            saved_id["value"] = note_id
            if index >= 0:
                notes[index] = {**notes[index], **note}
            else:
                notes.append(note)

            images = quick_note_images(snapshot)
            next_id = next_image_id(images)
            for pending in self.pending_images:
                image = {**pending}
                image["noteId"] = note_id
                image["updatedAt"] = now
                existing_index = -1
                if image.get("id"):
                    for i, row in enumerate(images):
                        if int(row.get("id") or 0) == int(image["id"]):
                            existing_index = i
                            break
                else:
                    image["id"] = next_id
                    next_id += 1

                if existing_index >= 0:
                    images[existing_index] = {**images[existing_index], **image}
                else:
                    images.append(image)

        def task() -> None:
            self.write_with_retry(mutate)
            self.selected_note_id = saved_id["value"]
            self.root.after(0, lambda: self.status_var.set(f"已同步云端版本 {self.revision}"))

        self.run_background(task)

    def delete_note(self) -> None:
        if not self.selected_note_id:
            return
        selected_id = self.selected_note_id

        def mutate(snapshot: dict[str, Any]) -> None:
            for note in quick_notes(snapshot):
                if int(note.get("id") or 0) == int(selected_id):
                    note["status"] = "DELETED"
                    note["isPinned"] = 0
                    note["updatedAt"] = int(time.time() * 1000)
                    return

        def task() -> None:
            self.write_with_retry(mutate)
            self.selected_note_id = None
            self.root.after(0, self.clear_editor)
            self.root.after(0, lambda: self.status_var.set(f"已删除并同步版本 {self.revision}"))

        self.run_background(task)

    def refresh_notes(self) -> None:
        notes = visible_notes(self.snapshot)
        self.note_list.delete(0, tk.END)
        image_counts: dict[int, int] = {}
        for image in quick_note_images(self.snapshot):
            if image.get("status") == "DELETED":
                continue
            note_id = int(image.get("noteId") or 0)
            image_counts[note_id] = image_counts.get(note_id, 0) + 1
        for note in notes[:12]:
            marker = "置顶 " if to_bool(note.get("isPinned")) else ""
            title = note.get("title") or default_title(note.get("createdAt"))
            image_text = f"  图{image_counts.get(int(note.get('id') or 0), 0)}" if image_counts.get(int(note.get("id") or 0), 0) else ""
            self.note_list.insert(tk.END, f"{marker}{format_time(note.get('updatedAt'))}  {title}{image_text}")
        self.note_list.notes = notes[:12]  # type: ignore[attr-defined]
        self.status_var.set(f"云端版本 {self.revision}，速记 {len(notes)} 条")
        self.delete_button.configure(state=tk.NORMAL if self.selected_note_id else tk.DISABLED)

    def on_note_selected(self, _event: tk.Event) -> None:
        selection = self.note_list.curselection()
        if not selection:
            return
        notes = getattr(self.note_list, "notes", [])
        if not notes:
            return
        note = notes[selection[0]]
        self.selected_note_id = int(note.get("id") or 0)
        self.inline_image_refs = []
        self.inline_image_widgets = []
        self.note_title.delete(0, tk.END)
        self.note_title.insert(0, note.get("title") or "")
        self.note_content.delete("1.0", tk.END)
        self.note_content.insert("1.0", note.get("content") or "")
        self.space_var.set(SPACE_LABELS.get(note.get("space"), "个人"))
        self.type_var.set(TYPE_LABELS.get(note.get("type"), "笔记"))
        self.favorite_var.set(to_bool(note.get("isFavorite")))
        self.pinned_var.set(to_bool(note.get("isPinned")))
        self.pending_images = [
            copy.deepcopy(image)
            for image in quick_note_images(self.snapshot)
            if int(image.get("noteId") or 0) == self.selected_note_id and image.get("status") != "DELETED"
        ]
        self.update_image_summary()
        self.render_pending_image_previews()
        self.delete_button.configure(state=tk.NORMAL)

    def clear_editor(self) -> None:
        self.selected_note_id = None
        self.inline_image_refs = []
        self.inline_image_widgets = []
        self.note_title.delete(0, tk.END)
        self.note_content.delete("1.0", tk.END)
        self.space_var.set("个人")
        self.type_var.set("笔记")
        self.favorite_var.set(False)
        self.pinned_var.set(False)
        self.pending_images = []
        self.update_image_summary()
        self.delete_button.configure(state=tk.DISABLED)
        self.status_var.set("正在新建笔记，保存后会同步到云端")
        self.note_content.focus_set()

    def add_images(self) -> None:
        paths = filedialog.askopenfilenames(
            title="选择图片",
            filetypes=[
                ("图片", "*.png;*.jpg;*.jpeg;*.webp;*.gif;*.bmp"),
                ("所有文件", "*.*"),
            ],
        )
        if not paths:
            return

        added = 0
        for raw_path in paths:
            if self.add_image_file(Path(raw_path)):
                added += 1

        if added:
            self.update_image_summary()
            self.status_var.set(f"已添加 {added} 张图片，保存后同步。")

    def paste_into_content(self, _event: tk.Event) -> str | None:
        try:
            clipboard = ImageGrab.grabclipboard()
        except Exception:
            return None

        if isinstance(clipboard, Image.Image):
            filename = f"clipboard-{datetime.now().strftime('%Y%m%d-%H%M%S')}.png"
            if self.add_pil_image(clipboard, filename):
                return "break"
            return None

        if isinstance(clipboard, list):
            added = 0
            for item in clipboard:
                path = Path(str(item))
                if path.is_file() and self.add_image_file(path):
                    added += 1
            if added:
                self.status_var.set(f"已粘贴 {added} 张图片，保存后同步。")
                return "break"

        return None

    def add_image_file(self, path: Path) -> bool:
        try:
            size = path.stat().st_size
            if size > MAX_IMAGE_BYTES:
                messagebox.showwarning("回溯速记", f"{path.name} 超过 5MB，已跳过。")
                return False
            data = path.read_bytes()
            image = Image.open(io.BytesIO(data))
            image.load()
        except Exception as error:
            messagebox.showwarning("回溯速记", f"读取图片失败：{path.name}\n{error}")
            return False

        mime_type = mimetypes.guess_type(path.name)[0] or Image.MIME.get(image.format, "application/octet-stream")
        self.add_pending_image(
            filename=path.name,
            mime_type=mime_type,
            data=data,
            preview=image,
        )
        return True

    def add_pil_image(self, image: Image.Image, filename: str) -> bool:
        buffer = io.BytesIO()
        try:
            image.convert("RGBA").save(buffer, format="PNG")
        except Exception as error:
            messagebox.showwarning("回溯速记", f"读取剪贴板图片失败：{error}")
            return False

        data = buffer.getvalue()
        if len(data) > MAX_IMAGE_BYTES:
            messagebox.showwarning("回溯速记", "剪贴板图片超过 5MB，已跳过。")
            return False

        self.add_pending_image(
            filename=filename,
            mime_type="image/png",
            data=data,
            preview=image,
        )
        self.status_var.set("已粘贴图片，保存后同步。")
        return True

    def add_pending_image(
        self,
        filename: str,
        mime_type: str,
        data: bytes,
        preview: Image.Image,
    ) -> None:
        now = int(time.time() * 1000)
        image_record = {
            "id": 0,
            "noteId": int(self.selected_note_id or 0),
            "fileName": filename,
            "mimeType": mime_type,
            "dataBase64": base64.b64encode(data).decode("ascii"),
            "sizeBytes": len(data),
            "status": "ACTIVE",
            "createdAt": now,
            "updatedAt": now,
        }
        self.pending_images.append(image_record)
        self.insert_image_preview(preview, filename, image_record["dataBase64"])
        self.update_image_summary()

    def insert_image_preview(self, image: Image.Image, filename: str, data_base64: str | None = None) -> None:
        preview = image.copy()
        preview.thumbnail((190, 130))
        photo = ImageTk.PhotoImage(preview)
        self.inline_image_refs.append(photo)

        if self.note_content.index("insert") != "1.0":
            self.note_content.insert("insert", "\n")

        label = tk.Label(
            self.note_content,
            image=photo,
            bg=WIN10_INPUT,
            cursor="hand2" if data_base64 else "arrow",
            borderwidth=1,
            relief="solid",
        )
        if data_base64:
            label.bind(
                "<Button-1>",
                lambda _event, payload=data_base64, title=filename: self.open_image_preview(payload, title),
            )
        self.inline_image_widgets.append(label)
        self.note_content.window_create("insert", window=label)
        self.note_content.insert("insert", "\n")
        self.note_content.focus_set()

    def render_pending_image_previews(self) -> None:
        for image in self.pending_images:
            if image.get("status") == "DELETED":
                continue
            try:
                raw = base64.b64decode(image.get("dataBase64") or "")
                preview = Image.open(io.BytesIO(raw))
                preview.load()
            except Exception:
                continue
            self.insert_image_preview(preview, image.get("fileName") or "image", image.get("dataBase64") or "")

    def open_image_preview(self, data_base64: str, title: str) -> None:
        try:
            raw = base64.b64decode(data_base64)
            image = Image.open(io.BytesIO(raw))
            image.load()
        except Exception as error:
            messagebox.showerror("回溯速记", f"打开图片失败：{error}")
            return

        window = tk.Toplevel(self.root)
        window.title(title or "图片预览")
        window.geometry("760x560")
        window.minsize(360, 260)
        window.configure(bg="#111111")
        window.attributes("-topmost", bool(self.topmost_var.get()))
        window.original_image = image  # type: ignore[attr-defined]
        window.photo_ref = None  # type: ignore[attr-defined]

        label = tk.Label(window, bg="#111111")
        label.pack(fill="both", expand=True, padx=10, pady=10)

        def render() -> None:
            width = max(120, label.winfo_width() - 8)
            height = max(120, label.winfo_height() - 8)
            preview = window.original_image.copy()  # type: ignore[attr-defined]
            preview.thumbnail((width, height))
            photo = ImageTk.PhotoImage(preview)
            window.photo_ref = photo  # type: ignore[attr-defined]
            label.configure(image=photo)

        window.bind("<Configure>", lambda _event: render())
        window.bind("<Escape>", lambda _event: window.destroy())
        window.after(80, render)

    def update_image_summary(self) -> None:
        count = len([image for image in self.pending_images if image.get("status") != "DELETED"])
        self.image_summary_var.set(f"图片 {count}")

    def open_settings(self) -> None:
        window = tk.Toplevel(self.root)
        window.title("同步设置")
        window.geometry("430x365")
        window.resizable(False, False)
        window.attributes("-topmost", bool(self.topmost_var.get()))
        try:
            window.attributes("-alpha", clamp_opacity(self.config.opacity))
        except Exception:
            pass
        window.configure(bg=WIN10_BG)

        frame = tk.Frame(window, bg=WIN10_BG)
        frame.pack(fill="both", expand=True, padx=14, pady=14)
        frame.columnconfigure(1, weight=1)

        tk.Label(frame, text="服务地址", bg=WIN10_BG, fg=WIN10_TEXT).grid(row=0, column=0, sticky="w", pady=6)
        server_var = tk.StringVar(value=self.config.server_url)
        ttk.Entry(frame, textvariable=server_var).grid(row=0, column=1, sticky="ew", pady=6)

        tk.Label(frame, text="Token", bg=WIN10_BG, fg=WIN10_TEXT).grid(row=1, column=0, sticky="w", pady=6)
        token_var = tk.StringVar(value=self.config.api_token)
        ttk.Entry(frame, textvariable=token_var, show="*").grid(row=1, column=1, sticky="ew", pady=6)

        tk.Label(frame, text="设备 ID", bg=WIN10_BG, fg=WIN10_TEXT).grid(row=2, column=0, sticky="w", pady=6)
        device_var = tk.StringVar(value=self.config.device_id)
        ttk.Entry(frame, textvariable=device_var).grid(row=2, column=1, sticky="ew", pady=6)

        tk.Label(frame, text="加密密码", bg=WIN10_BG, fg=WIN10_TEXT).grid(row=3, column=0, sticky="w", pady=6)
        password_var = tk.StringVar(value=self.config.encryption_password)
        ttk.Entry(frame, textvariable=password_var, show="*").grid(row=3, column=1, sticky="ew", pady=6)

        tk.Label(frame, text="透明度", bg=WIN10_BG, fg=WIN10_TEXT).grid(row=4, column=0, sticky="w", pady=6)
        opacity_var = tk.IntVar(value=int(clamp_opacity(self.config.opacity) * 100))
        opacity_scale = tk.Scale(
            frame,
            from_=78,
            to=100,
            orient="horizontal",
            variable=opacity_var,
            bg=WIN10_BG,
            fg=WIN10_TEXT,
            troughcolor=WIN10_PANEL,
            highlightthickness=0,
            activebackground=WIN10_ACCENT,
        )
        opacity_scale.grid(row=4, column=1, sticky="ew", pady=2)

        hint = tk.Label(
            frame,
            text="如果手机端没有设置快照加密密码，这里留空。",
            bg=WIN10_BG,
            fg=WIN10_MUTED,
            anchor="w",
        )
        hint.grid(row=5, column=0, columnspan=2, sticky="ew", pady=(4, 12))

        actions = tk.Frame(frame, bg=WIN10_BG)
        actions.grid(row=6, column=0, columnspan=2, sticky="e")

        def apply_config() -> None:
            self.config.server_url = server_var.get().strip()
            self.config.api_token = token_var.get().strip()
            self.config.device_id = device_var.get().strip() or f"windows-{uuid.uuid4().hex[:8]}"
            self.config.encryption_password = password_var.get()
            self.config.opacity = clamp_opacity(opacity_var.get() / 100)
            self.config.save()
            self.client = SyncClient(self.config)
            self.apply_opacity()
            try:
                window.attributes("-alpha", self.config.opacity)
            except Exception:
                pass

        def save_and_close() -> None:
            try:
                apply_config()
                self.status_var.set("设置已保存")
                window.destroy()
            except Exception as error:
                messagebox.showerror("回溯速记", str(error), parent=window)

        def test_connection() -> None:
            try:
                apply_config()
                self.client.probe()
                messagebox.showinfo("回溯速记", "连接正常，Token 可用。", parent=window)
            except Exception as error:
                messagebox.showerror("回溯速记", str(error), parent=window)

        tk.Button(actions, text="测试连接", command=test_connection, relief="flat", bg=WIN10_PANEL).pack(side="left", padx=(0, 8))
        tk.Button(actions, text="保存", command=save_and_close, bg=WIN10_ACCENT, fg="white", activebackground=WIN10_ACCENT_DARK, activeforeground="white", relief="flat", width=9).pack(side="left")


def enable_dpi_awareness() -> None:
    try:
        import ctypes

        ctypes.windll.shcore.SetProcessDpiAwareness(1)
    except Exception:
        pass


def main() -> None:
    enable_dpi_awareness()
    app = StickyNoteApp()
    app.run()


if __name__ == "__main__":
    main()
