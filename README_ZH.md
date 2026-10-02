# FLOXIN NET

FLOXIN NET 是一款开源本地 DNS 工具，提供广告拦截、DNS 缓存、网络质量分析和基于测量的优化建议，支持 Linux、macOS、Android 终端、WSL、Docker 和 proot，无需 root。

## 安装

```bash
git clone https://github.com/FL0XIN/FLOXIN-NET.git
cd FLOXIN-NET
./setup.sh
```

## 工具和命令

- `FLOXIN help` — 显示完整命令目录。
- `DNSMGR start|stop|restart|status|test` — 管理本地 DNS。
- `NETGUARD start --hours 8` — 启用临时 DNS 保护。
- `NETGUARD status|log|stop` — 查看状态、日志或停止。
- `NETSCAN network|quality|latency|jitter|packet-loss|dpi-probe|qos-detect` — 只读网络分析。
- `NETOPT auto|mtu-probe|mtu-set|tcp-tuning|port-test|route-test|dns-optimize|protocol|status|reset` — 测量型优化建议。
- `NETTURBO scan|best|apply|monitor|auto|status|stop|history|report` — 比较八个 HTTPS 端口。
- `NETCHECK` — 完整环境和连接检查。

NETTURBO 使用固定的 `speed.cloudflare.com/__down` 比较源和 500KB 载荷。它不会保证提高带宽，也不会透明地重定向所有系统流量，只会为支持端口选择的客户端保存建议。

## 文档

- [网络分析](docs/NETWORK_ANALYSIS.md)
- [NETTURBO](docs/NETTURBO.md)
- [项目结构](docs/PROJECT_TREE.md)
- [脚本目录](docs/SCRIPT_INVENTORY.md)
- [English README](README.md)
- [العربية](README_AR.md)
- [Русский](README_RU.md)

## 隐私和安全

默认 DNS 地址为 `127.0.0.1:5353`。请审查 DNS 提供商和拦截列表，并把查询日志视为私密数据。本项目不创建 VPN，也不绕过运营商政策。

## 许可证

MIT — 查看 [LICENSE](LICENSE)。
