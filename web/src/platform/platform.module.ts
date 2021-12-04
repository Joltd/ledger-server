import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserComponent } from './component/browser/browser.component';
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {HttpClientModule} from "@angular/common/http";
import {TranslocoRootModule} from "../app/transloco-root.module";
import {MatSortModule} from "@angular/material/sort";

@NgModule({
    declarations: [
        BrowserComponent
    ],
    exports: [
        BrowserComponent
    ],
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    HttpClientModule,
    TranslocoRootModule,
    MatSortModule
  ]
})
export class PlatformModule { }
