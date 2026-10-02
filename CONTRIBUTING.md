# Contributing

1. Keep the existing Termux commands working.
2. Test Bash syntax with `bash -n` and Python syntax with `python3 -m py_compile`.
3. Do not commit blocklists, logs, personal configuration, or backup files.
4. Keep network downloads explicit, bounded by timeouts, and documented.
5. Prefer localhost binding unless a user explicitly configures a trusted network.
6. Update `CHANGELOG.md` for user-visible changes.

Bug reports should include the output of `FLOXIN env` with public IP and personal data removed.
