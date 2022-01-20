import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {EntityBrowserComponent} from "../platform/component/entity-browser/entity-browser.component";
import {EntityEditorComponent} from "../platform/component/entity-editor/entity-editor.component";
import {entityBrowserMatcher, entityEditorMatcher} from "../platform/service/entity-provider.service";
import {EntityListComponent} from "../platform/component/entity-list/entity-list.component";

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
