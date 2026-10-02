# FLOXIN NET

أداة مفتوحة المصدر لحجب الإعلانات عبر DNS محلي، وتحليل جودة الشبكة، وقياس المنافذ، وتقديم توصيات آمنة بدون صلاحيات root.

## التثبيت

```bash
git clone https://github.com/FL0XIN/FLOXIN-NET.git
cd FLOXIN-NET
./setup.sh
```

## الأدوات والأوامر

- `FLOXIN help` — عرض قائمة الأوامر الكاملة.
- `DNSMGR start|stop|restart|status|test` — إدارة DNS المحلي.
- `NETGUARD start --hours 8` — حماية DNS مؤقتة من نطاقات التتبع والإعلانات.
- `NETGUARD status|log|stop` — الحالة والسجل والإيقاف.
- `NETSCAN network|quality|latency|jitter|packet-loss|dpi-probe|qos-detect` — تحليل الشبكة بدون تعديل صامت.
- `NETOPT auto|mtu-probe|mtu-set|tcp-tuning|port-test|route-test|dns-optimize|protocol|status|reset` — توصيات مبنية على القياس.
- `NETTURBO scan|best|apply|monitor|auto|status|stop|history|report` — مقارنة المنافذ HTTPS الثمانية.
- `NETCHECK` — فحص شامل للبيئة والاتصال.

يستخدم NETTURBO مصدر مقارنة ثابتًا هو `speed.cloudflare.com/__down` مع حمولة 500KB. لا يضمن زيادة السرعة ولا يعيد توجيه حركة النظام بالكامل، بل يحفظ اقتراحًا لعملاء يدعمون اختيار المنفذ.

## التوثيق

- [تحليل الشبكة](docs/NETWORK_ANALYSIS.md)
- [توثيق NETTURBO](docs/NETTURBO.md)
- [شجرة المشروع](docs/PROJECT_TREE.md)
- [فهرس السكربتات](docs/SCRIPT_INVENTORY.md)
- [English README](README.md)
- [简体中文](README_ZH.md)
- [Русский](README_RU.md)

## ملاحظات الخصوصية

يعمل DNS على `127.0.0.1:5353` افتراضيًا. راجع مزود DNS وقائمة الحجب قبل الاستخدام، وتعامل مع سجلات الاستعلامات كبيانات خاصة. المشروع لا ينشئ VPN ولا يتجاوز سياسات مزود الخدمة.

## الترخيص

MIT — راجع [LICENSE](LICENSE).
