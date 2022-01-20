import { Component, OnInit } from '@angular/core';
import {EntityProviderService} from "../../service/entity-provider.service";
import {TranslocoService} from "@ngneat/transloco";

@Component({
  selector: 'entity-list',
  templateUrl: './entity-list.component.html',
  styleUrls: ['./entity-list.component.scss']
})
export class EntityListComponent implements OnInit {

  constructor(
    public entityProviderService: EntityProviderService,
    public translocoService: TranslocoService
  ) {}

  ngOnInit(): void {
    this.entityProviderService.loadMeta()
  }

}
