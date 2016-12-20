#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_example_adlr_lightmeter_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Frequency: 120.14Hz\nFlicker Index: 10%\nFlicker Ratio: 20%";
    return env->NewStringUTF(hello.c_str());
}
