#!/usr/bin/env bash
# NETOPT shell integration defaults. Changes are proposals unless explicitly applied by the OS.
export FLOXIN_NETOPT_DIR="${FLOXIN_NETOPT_DIR:-$HOME/.floxin_netopt}"
net_opt_help() { "$HOME/bin/NETOPT" --help; }
