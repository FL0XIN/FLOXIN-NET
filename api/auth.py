from __future__ import annotations

import time
from collections import defaultdict, deque

from fastapi import Header, HTTPException, status

from .config import ensure_token


class RateLimiter:
    def __init__(self, limit: int = 60, window: int = 60):
        self.limit = limit
        self.window = window
        self.requests: dict[str, deque[float]] = defaultdict(deque)

    def check(self, token: str) -> None:
        now = time.monotonic()
        bucket = self.requests[token]
        while bucket and now - bucket[0] >= self.window:
            bucket.popleft()
        if len(bucket) >= self.limit:
            raise HTTPException(status_code=429, detail="Rate limit exceeded")
        bucket.append(now)


limiter = RateLimiter()


def require_token(authorization: str | None = Header(default=None)) -> str:
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Bearer token required")
    token = authorization.removeprefix("Bearer ").strip()
    if not token or token != ensure_token():
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")
    limiter.check(token)
    return token
