
gradle --daemon assembleDebug

adb install -r build/apk/blokish-debug-unaligned.apk

adb shell am start -n org.scoutant.blokish/org.scoutant.blokish.UI
