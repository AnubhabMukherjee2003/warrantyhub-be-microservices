#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MAIN_SRC="$ROOT_DIR/MAIN/warrantyhub-be/src/main/java/com/anubhab/warrantyhub"

copy_business_service() {
  local service_dir="$1"
  local package_name="$2"
  local package_path="$3"
  local target="$ROOT_DIR/API/$service_dir/src/main/java/$package_path"

  mkdir -p "$target"
  cp -R "$MAIN_SRC"/config "$target"/
  cp -R "$MAIN_SRC"/controller "$target"/
  cp -R "$MAIN_SRC"/dto "$target"/
  cp -R "$MAIN_SRC"/exception "$target"/
  cp -R "$MAIN_SRC"/model "$target"/
  cp -R "$MAIN_SRC"/repository "$target"/
  cp -R "$MAIN_SRC"/security "$target"/
  cp -R "$MAIN_SRC"/service "$target"/

  find "$target" -type f -name '*.java' -print0 |
    xargs -0 sed -i "s/package com\\.anubhab\\.warrantyhub/package $package_name/g; s/import com\\.anubhab\\.warrantyhub/import $package_name/g"
}

copy_business_service "customerCompanyService" "com.warrantyhub.customerCompanyService" "com/warrantyhub/customerCompanyService"
copy_business_service "productWarrantyPurchaseService" "com.warrantyhub.productWarrantyPurchaseService" "com/warrantyhub/productWarrantyPurchaseService"
copy_business_service "serviceRequestService" "com.warrantyhub.serviceRequestService" "com/warrantyhub/serviceRequestService"

echo "Copied monolith source into API business services."
