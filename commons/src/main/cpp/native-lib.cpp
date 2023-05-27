#include <jni.h>
#include <string>
#include <unistd.h>
#include <android/log.h>

#define TAG "YOURAPPTAG"

#include <stdlib.h>


char *en(const char *input) {
    int len = strlen(input);
    int leftover = len % 3;
    char *ret = (char *) malloc(((len / 3) * 4) + ((leftover) ? 4 : 0) + 1);
    int n = 0;
    int outlen = 0;
    uint8_t i = 0;
    uint8_t *inp = (uint8_t *) input;
    const char *index = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        "abcdefghijklmnopqrstuvwxyz"
                        "0123456789+/";

    if (ret == NULL)
        return NULL;

    len -= leftover;
    for (n = 0; n < len; n += 3) {
        i = inp[n] >> 2;
        ret[outlen++] = index[i];

        i = (inp[n] & 0x03) << 4;
        i |= (inp[n + 1] & 0xf0) >> 4;
        ret[outlen++] = index[i];

        i = ((inp[n + 1] & 0x0f) << 2);
        i |= ((inp[n + 2] & 0xc0) >> 6);
        ret[outlen++] = index[i];

        i = (inp[n + 2] & 0x3f);
        ret[outlen++] = index[i];
    }

    // Handle leftover 1 or 2 bytes.
    if (leftover) {
        i = (inp[n] >> 2);
        ret[outlen++] = index[i];

        i = (inp[n] & 0x03) << 4;
        if (leftover == 2) {
            i |= (inp[n + 1] & 0xf0) >> 4;
            ret[outlen++] = index[i];

            i = ((inp[n + 1] & 0x0f) << 2);
        }
        ret[outlen++] = index[i];
        ret[outlen++] = '=';
        if (leftover == 1)
            ret[outlen++] = '=';
    }
    ret[outlen] = '\0';
    return ret;
}


std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes,
                                                                       env->NewStringUTF("UTF-8"));

    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte *pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char *) pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_hashone_allinone_retrofit_RetrofitHelper_show(JNIEnv *env, jobject type) {
    pid_t pid = getpid();
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "process id %d\n", pid);
    char path[64] = {0};
    sprintf(path, "/proc/%d/cmdline", pid);
    FILE *cmdline = fopen(path, "r");
    if (cmdline) {
        char a[64] = {0};
        fread(a, sizeof(a), 1, cmdline);
        fclose(cmdline);

        const char *charBase = en(a);

        jstring jstringBase = env->NewStringUTF(charBase);

        std::string stringBase = jstring2string(env, jstringBase);

//        solution 1 for appending string in between
//        char* strB = (char*)"123", strC[100];
//        int x = 4;
//        strncpy(strC,test,x);
//        strC[x] = '\0';
//        strcat(strC,strB);
//        strcat(strC,test+x);
//        __android_log_print(ANDROID_LOG_DEBUG, TAG, "process id %s\n", strC);

//        solution 2 for appending string in between
        stringBase.insert(5, "bh5d");
        stringBase = "ak4Yb" + stringBase + "iA7k==";
//        __android_log_print(ANDROID_LOG_DEBUG, TAG, "process id %s\n", stringBase.c_str());

        const char *charMoreBase = en(stringBase.c_str());
//        jstring jsstringMoreBase = env->NewStringUTF(charMoreBase);
//        std::string stringMoreBase= jstring2string(env, jsstringMoreBase);
        return env->NewStringUTF(charMoreBase);
    }
}