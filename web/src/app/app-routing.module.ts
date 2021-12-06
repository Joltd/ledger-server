import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {OrderBrowserComponent} from "./reference/order/browser/order-browser/order-browser.component";

const routes: Routes = [
  { path: 'order', component: OrderBrowserComponent },
  { path: '', redirectTo: '/order', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
