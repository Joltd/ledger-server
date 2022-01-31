import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {EntityBrowserComponent} from "./component/entity-browser/entity-browser.component";
import {EntityEditorComponent} from "./component/entity-editor/entity-editor.component";
import {entityBrowserMatcher, entityEditorMatcher} from "./service/entity-provider.service";
import {EntityListComponent} from "./component/entity-list/entity-list.component";

const routes: Routes = [
  { path: 'entity', component: EntityListComponent },
  { matcher: entityBrowserMatcher, component: EntityBrowserComponent },
  { matcher: entityEditorMatcher, component: EntityEditorComponent },
  { path: '', redirectTo: '/entity', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
