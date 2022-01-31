import { Component, OnInit } from '@angular/core';
import {EntityProviderService} from "../../service/entity-provider.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'entity-browser',
  templateUrl: './entity-browser.component.html',
  styleUrls: ['./entity-browser.component.scss']
})
export class EntityBrowserComponent implements OnInit {

  constructor(
    public entityProvider: EntityProviderService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.params
      .subscribe(params => {
        this.entityProvider.setupEntity(params['entity'], undefined)
      })
  }

}
