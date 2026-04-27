package com.app.huisu.data.cloud

import android.util.Base64
import org.json.JSONObject
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSnapshotCipher @Inject constructor() {
    fun encrypt(plainText: String, password: String): String {
        val salt = randomBytes(SALT_SIZE)
        val iv = randomBytes(IV_SIZE)
        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_SIZE_BITS, iv))
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        return JSONObject()
            .put("encrypted", true)
            .put("algorithm", "AES-256-GCM")
            .put("kdf", "PBKDF2WithHmacSHA256")
            .put("iterations", ITERATIONS)
            .put("salt", salt.toBase64())
            .put("iv", iv.toBase64())
            .put("ciphertext", encrypted.toBase64())
            .toString()
    }

    fun decrypt(payload: String, password: String): String {
        val wrapper = JSONObject(payload)
        if (!wrapper.optBoolean("encrypted", false)) return payload
        require(password.isNotBlank()) { "云端快照已加密，请填写快照加密密码" }

        val salt = wrapper.getString("salt").fromBase64()
        val iv = wrapper.getString("iv").fromBase64()
        val encrypted = wrapper.getString("ciphertext").fromBase64()
        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE_BITS, iv))
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE_BITS)
        val secret = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec)
        return SecretKeySpec(secret.encoded, "AES")
    }

    private fun randomBytes(size: Int): ByteArray {
        return ByteArray(size).also { SecureRandom().nextBytes(it) }
    }

    private fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

    private fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
        private const val SALT_SIZE = 16
        private const val TAG_SIZE_BITS = 128
        private const val KEY_SIZE_BITS = 256
        private const val ITERATIONS = 120_000
    }
}
