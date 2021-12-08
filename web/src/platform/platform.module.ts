import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BrowserComponent} from './component/browser/browser.component';
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {HttpClientModule} from "@angular/common/http";
import {TranslocoRootModule} from "../app/transloco-root.module";
import {MatSortModule} from "@angular/material/sort";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {OverlayService} from "./service/overlay.service";
import {BrowserSettingsComponent} from './component/browser-settings/browser-settings.component';
import {MatTabsModule} from "@angular/material/tabs";
import {MatListModule} from "@angular/material/list";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTreeModule} from "@angular/material/tree";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {MatInputModule} from "@angular/material/input";
import {MatMenuModule} from "@angular/material/menu";
import {EditorComponent} from './component/editor/editor.component';
import {MatCardModule} from "@angular/material/card";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {ObjectSelectorComponent} from './component/object-selector/object-selector.component';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatNativeDateModule} from "@angular/material/core";

@NgModule({
  declarations: [
    BrowserComponent,
    BrowserSettingsComponent,
    EditorComponent,
    ObjectSelectorComponent
  ],
  exports: [
    BrowserComponent,
    EditorComponent
  ],
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    HttpClientModule,
    TranslocoRootModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatListModule,
    FormsModule,
    MatTreeModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatMenuModule,
    MatCardModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule
  ],
  providers: [
    OverlayService
  ]
})
export class PlatformModule {}
