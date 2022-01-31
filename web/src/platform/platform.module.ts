import {NgModule} from '@angular/core';
import {BrowserComponent} from './component/browser/browser.component';
import {OverlayService} from "./service/overlay.service";
import {BrowserSettingsComponent} from './component/browser-settings/browser-settings.component';
import {EditorComponent} from './component/editor/editor.component';
import {ObjectSelectorComponent} from './component/object-selector/object-selector.component';
import {EnumSelectorComponent} from './component/enum-selector/enum-selector.component';
import {MatCardModule} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {CommonModule} from "@angular/common";
import {MatSortModule} from "@angular/material/sort";
import {TranslocoRootModule} from "./transloco-root.module";
import {MatMenuModule} from "@angular/material/menu";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatTabsModule} from "@angular/material/tabs";
import {MatListModule} from "@angular/material/list";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTreeModule} from "@angular/material/tree";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {MatInputModule} from "@angular/material/input";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatAutocompleteModule} from "@angular/material/autocomplete";

@NgModule({
  declarations: [
    BrowserComponent,
    BrowserSettingsComponent,
    EditorComponent,
    ObjectSelectorComponent,
    EnumSelectorComponent
  ],
  exports: [
    BrowserComponent,
    EditorComponent,
    ObjectSelectorComponent
  ],
  imports: [
    MatCardModule,
    MatTableModule,
    CommonModule,
    MatSortModule,
    TranslocoRootModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    MatPaginatorModule,
    MatTabsModule,
    MatListModule,
    FormsModule,
    MatTreeModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatSlideToggleModule,
    MatAutocompleteModule

  ],
  providers: [
    OverlayService
  ]
})
export class PlatformModule {}
