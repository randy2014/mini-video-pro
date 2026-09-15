#!/bin/bash
# 反查 APK 里真实编译进去的 API 域名，确认包确实是新域名版本
set -u
APK="/data/video-apk/video-entitlement-v0.9.202609151543-20260915-154435.apk"

python3 - "$APK" <<'PYEOF'
import sys, zipfile, re
apk = sys.argv[1]
z = zipfile.ZipFile(apk)
dexes = [n for n in z.namelist() if re.match(r'classes\d*\.dex$', n)]
print("=== dex 文件 ===")
print(" ", dexes)

blob = b"".join(z.read(n) for n in dexes)
print()
print("=== APK 内域名常量出现次数 ===")
for pat in [b'https://xs2026.site', b'http://64.90.19.6:8081', b'64.90.19.6:8082']:
    print("  %-28s %d 次" % (pat.decode(), blob.count(pat)))

print()
print("=== xs2026.site 上下文 ===")
for m in list(re.finditer(rb'xs2026\.site', blob))[:6]:
    s = max(0, m.start()-24); e = min(len(blob), m.end()+28)
    print("  ", blob[s:e].decode('utf-8', 'replace'))
PYEOF
