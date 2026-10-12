#!/usr/bin/env python3
"""Detect drift between the pinned OpenAPI contract and this implementation.

Checks, against the contract version pinned in the root pom
(<backend-core-api.version>):
  1. Version freshness: warns if the pin is behind the latest
     app-backend-contracts release.
  2. Path coverage: every OAS path must have a matching @Path in
     adapters-inbound-rest (prefix match, so /room-reports covers
     /room-reports/{roomReportId}). Documented-but-unimplemented paths are
     reported as warnings (work in progress); implemented-but-undocumented
     paths FAIL the check (rogue endpoints bypass the contract).

Usage: check-contract-drift.py [--contracts-repo owner/name] [--fail-on-warn]
Requires network access to api.github.com / raw.githubusercontent.com.
Exits 0 unless implemented-but-undocumented paths exist (or --fail-on-warn).
"""

import re
import sys
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DEFAULT_REPO = "neuland-ingolstadt/app-backend-contracts"


def gh_get(url: str) -> bytes:
    req = urllib.request.Request(
        url, headers={"Accept": "application/vnd.github+json"}
    )
    with urllib.request.urlopen(req, timeout=30) as resp:
        return resp.read()


def pinned_version() -> str:
    pom = ET.parse(ROOT / "pom.xml")
    ns = {"m": "http://maven.apache.org/POM/4.0.0"}
    el = pom.getroot().find("m:properties/m:backend-core-api.version", ns)
    if el is None or not el.text:
        raise SystemExit("backend-core-api.version not found in root pom.xml")
    return el.text.strip()


def oas_paths(oas_text: str) -> list[str]:
    # Top-level paths are the only two-space-indented "/..." keys before
    # "components:".
    paths_section = oas_text.split("\ncomponents:", 1)[0]
    return re.findall(r"^  (/[^\s:]*):?\s*$", paths_section, re.MULTILINE)


def implemented_paths() -> set[str]:
    found: set[str] = set()
    base = ROOT / "adapters-inbound-rest" / "src" / "main" / "java"
    for java in base.rglob("*.java"):
        found.update(re.findall(r'@Path\("([^"]+)"\)', java.read_text()))
    return found


def covers(oas_path: str, impl: set[str]) -> bool:
    prefix = oas_path.split("{", 1)[0].rstrip("/") or "/"
    return any(
        p == prefix or p.startswith(prefix + "/") or prefix.startswith(p + "/")
        for p in impl
    )


def main() -> int:
    repo = sys.argv[sys.argv.index("--contracts-repo") + 1] \
        if "--contracts-repo" in sys.argv else DEFAULT_REPO
    fail_on_warn = "--fail-on-warn" in sys.argv

    version = pinned_version()
    print(f"Pinned contract version: {version}")

    import json

    latest = json.loads(
        gh_get(f"https://api.github.com/repos/{repo}/releases/latest")
    )["tag_name"].lstrip("v")
    print(f"Latest contracts release: {latest}")
    stale = latest != version
    if stale:
        print(f"::warning::Pinned contract {version} is behind {latest}.")

    oas_url = (
        f"https://raw.githubusercontent.com/{repo}/v{version}"
        f"/oas/backend-core-api-v0.yaml"
    )
    spec = urllib.request.urlopen(oas_url, timeout=30).read().decode()
    spec_paths = oas_paths(spec)
    impl = implemented_paths()
    print(f"OAS paths ({len(spec_paths)}): {sorted(spec_paths)}")
    print(f"Implemented @Path values: {sorted(impl)}")

    missing_impl = [p for p in spec_paths if not covers(p, impl)]
    rogue = sorted(
        p for p in impl
        if not any(covers(o, {p}) for o in spec_paths)
    )

    for p in missing_impl:
        print(f"::warning::OAS path {p} has no implementation yet.")
    for p in rogue:
        print(f"::error::Implemented path {p} is not in the OAS contract.")

    if rogue or (fail_on_warn and (missing_impl or stale)):
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
