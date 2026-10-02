# FLOXIN NET

FLOXIN NET — открытый локальный DNS-инструмент для блокировки рекламы, кэширования DNS, анализа качества сети и рекомендаций на основе измерений. Работает в Linux, macOS, Android Terminal, WSL, Docker и proot без root.

## Установка

```bash
git clone https://github.com/FL0XIN/FLOXIN-NET.git
cd FLOXIN-NET
./setup.sh
```

## Инструменты и команды

- `FLOXIN help` — полный каталог команд.
- `DNSMGR start|stop|restart|status|test` — управление локальным DNS.
- `NETGUARD start --hours 8` — временная DNS-защита.
- `NETGUARD status|log|stop` — состояние, журнал и остановка.
- `NETSCAN network|quality|latency|jitter|packet-loss|dpi-probe|qos-detect` — измерительный анализ сети.
- `NETOPT auto|mtu-probe|mtu-set|tcp-tuning|port-test|route-test|dns-optimize|protocol|status|reset` — рекомендации по оптимизации.
- `NETTURBO scan|best|apply|monitor|auto|status|stop|history|report` — сравнение восьми HTTPS-портов.
- `NETCHECK` — полная проверка окружения и подключения.

NETTURBO использует фиксированный источник сравнения `speed.cloudflare.com/__down` и загрузку 500 КБ. Инструмент не обещает увеличение пропускной способности и не перенаправляет прозрачно весь системный трафик, а сохраняет рекомендацию для совместимых клиентов.

## Документация

- [Анализ сети](docs/NETWORK_ANALYSIS.md)
- [NETTURBO](docs/NETTURBO.md)
- [Дерево проекта](docs/PROJECT_TREE.md)
- [Каталог скриптов](docs/SCRIPT_INVENTORY.md)
- [English README](README.md)
- [العربية](README_AR.md)
- [简体中文](README_ZH.md)

## Безопасность и приватность

DNS по умолчанию работает на `127.0.0.1:5353`. Проверяйте DNS-провайдера и список блокировки, а журналы запросов считайте приватными данными. Проект не создает VPN и не обходит политики провайдера.

## Лицензия

MIT — см. [LICENSE](LICENSE).
