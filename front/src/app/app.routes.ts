import { Routes } from "@angular/router";
import { HomeComponent } from "./features/home/components/home.component";
import { ProductListComponent } from "./features/products/components/product-list.component";
import { ClientRegistrationComponent } from "./features/clients/components/client-registration.component";
import { ClientLoginComponent } from "./features/clients/components/client-login.component";
import { OrderListComponent } from "./features/orders/components/order-list.component";
import { PendingDeliveryComponent } from "./features/orders/components/pending-delivery.component";
import { KitListComponent } from "./features/kits/components/kit-list.component";
import { BestSellersReportComponent } from "./features/reports/components/best-sellers-report.component";
import { MinimumStockReportComponent } from "./features/reports/components/minimum-stock-report.component";
import { CouponManagerComponent } from "./features/coupons/components/coupon-manager.component";
import { OrderHistoryComponent } from "./features/orders/components/order-history.component";
import { AuthGuard } from "./core/services/auth.guard";
import { AdminGuard } from "./core/services/admin.guard";

export const routes: Routes = [
  { path: "", component: HomeComponent, pathMatch: "full" },
  { path: "products", redirectTo: "products/usuario", pathMatch: "full" },
  { path: "products/:role", component: ProductListComponent },
  { path: "orders", component: OrderListComponent, canActivate: [AdminGuard] },
  { path: "orders/pendientes", component: PendingDeliveryComponent, canActivate: [AdminGuard] },
  { path: "kits", component: KitListComponent, canActivate: [AdminGuard] },
  { path: "coupons", component: CouponManagerComponent, canActivate: [AdminGuard] },
  { path: "reports/best-sellers", component: BestSellersReportComponent, canActivate: [AdminGuard] },
  { path: "reports/minimum-stock", component: MinimumStockReportComponent, canActivate: [AdminGuard] },
  { path: "mis-pedidos", component: OrderHistoryComponent, canActivate: [AuthGuard] },
  { path: "register", component: ClientRegistrationComponent },
  { path: "login", component: ClientLoginComponent },
  { path: "**", redirectTo: "" }
];
