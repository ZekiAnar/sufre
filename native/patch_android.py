# Adds Süfrə's native parts to the generated Capacitor Android project.
import os, re, shutil, sys
A = 'android/app/src/main'
J = A + '/java/az/sufre/app'
for f in ['MainActivity.java', 'SufreWidget.java', 'SufreWidgetPlugin.java']:
    shutil.copy('native/' + f, J + '/' + f)
for sub in ['layout', 'xml', 'drawable']:
    os.makedirs(A + '/res/' + sub, exist_ok=True)
    for f in os.listdir('native/res/' + sub):
        shutil.copy('native/res/%s/%s' % (sub, f), A + '/res/%s/%s' % (sub, f))

m = open(A + '/AndroidManifest.xml', encoding='utf-8').read()
if 'xmlns:tools' not in m:
    m = m.replace('<manifest xmlns:android="http://schemas.android.com/apk/res/android"',
                  '<manifest xmlns:android="http://schemas.android.com/apk/res/android" xmlns:tools="http://schemas.android.com/tools"', 1)
receiver = '''
        <receiver android:name=".SufreWidget" android:exported="false">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data android:name="android.appwidget.provider" android:resource="@xml/sufre_widget_info" />
        </receiver>
    </application>'''
if 'SufreWidget' not in m:
    m = m.replace('    </application>', receiver, 1)
# Health Connect: keep only the read permissions the app uses (steps, weight)
plug = open('node_modules/@capgo/capacitor-health/android/src/main/AndroidManifest.xml', encoding='utf-8').read()
keep = {'android.permission.health.READ_STEPS', 'android.permission.health.READ_WEIGHT'}
rm = [p for p in re.findall(r'android:name="(android\.permission\.health\.[A-Z_]+)"', plug) if p not in keep]
lines = ''.join('    <uses-permission android:name="%s" tools:node="remove" />\n' % p for p in sorted(set(rm)))
if 'tools:node="remove"' not in m:
    m = m.replace('</manifest>', lines + '</manifest>')
open(A + '/AndroidManifest.xml', 'w', encoding='utf-8').write(m)

s = open(A + '/res/values/strings.xml', encoding='utf-8').read()
if 'health_connect_privacy_policy_url' not in s:
    s = s.replace('</resources>', '    <string name="health_connect_privacy_policy_url">https://zekianar.github.io/sufre/privacy.html</string>\n</resources>')
open(A + '/res/values/strings.xml', 'w', encoding='utf-8').write(s)

v = open('android/variables.gradle', encoding='utf-8').read()
v = re.sub(r'minSdkVersion = \d+', 'minSdkVersion = 26', v)
open('android/variables.gradle', 'w', encoding='utf-8').write(v)
print('patched; removed %d health permissions' % len(set(rm)))
