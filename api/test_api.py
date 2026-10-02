from __future__ import annotations

import os
import tempfile
import unittest
from pathlib import Path


class ApiSmokeTest(unittest.TestCase):
    def test_config_and_database(self):
        with tempfile.TemporaryDirectory() as tmp:
            os.environ["FLOXIN_CONFIG_DIR"] = tmp
            os.environ["FLOXIN_CONFIG_PATH"] = str(Path(tmp) / "config.json")
            os.environ["FLOXIN_TOKEN_PATH"] = str(Path(tmp) / "token")
            os.environ["FLOXIN_DB_PATH"] = str(Path(tmp) / "stats.db")
            from api.config import ensure_config, ensure_token
            from api.database import Database
            self.assertEqual(ensure_config()["mode"], "DATA_SAVER")
            self.assertGreaterEqual(len(ensure_token()), 32)
            db = Database(Path(tmp) / "stats.db")
            db.add_query("A", "ads.example", "BLOCK", "0.0.0.0")
            db.add_query("A", "example.com", "OK", "93.184.216.34")
            stats = db.stats(2)
            self.assertEqual(stats["total_queries"], 2)
            self.assertEqual(stats["blocked"], 1)
            self.assertEqual(len(db.recent(10)), 2)


if __name__ == "__main__":
    unittest.main()
