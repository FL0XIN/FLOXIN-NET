#!/usr/bin/env bash
# NETSCAN shell integration defaults.
export FLOXIN_NETSCAN_TARGET="${FLOXIN_NETSCAN_TARGET:-1.1.1.1}"
net_scan_help() { "$HOME/bin/NETSCAN" --help; }
