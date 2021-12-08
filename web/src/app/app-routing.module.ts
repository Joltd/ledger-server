import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {OrderBrowserComponent} from "./reference/order/component/order-browser/order-browser.component";
import {OrderEditorComponent} from "./reference/order/component/order-editor/order-editor.component";

const routes: Routes = [
  { path: 'order', component: OrderBrowserComponent },
  { path: 'order/:id', component: OrderEditorComponent },
  { path: '', redirectTo: '/order', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
