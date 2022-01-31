import { Component, OnInit } from '@angular/core';
import {EntityProviderService} from "../../service/entity-provider.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'entity-editor',
  templateUrl: './entity-editor.component.html',
  styleUrls: ['./entity-editor.component.scss']
})
export class EntityEditorComponent implements OnInit {

  constructor(
    public entityProvider: EntityProviderService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.params
      .subscribe(params => {
        this.entityProvider.setupEntity(params['entity'], params['id'])
      })
  }

}
