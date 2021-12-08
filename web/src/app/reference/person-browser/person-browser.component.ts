import { Component, OnInit } from '@angular/core';
import {Descriptor} from "../../../platform/model/descriptor";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'person-browser',
  templateUrl: './person-browser.component.html',
  styleUrls: ['./person-browser.component.scss']
})
export class PersonBrowserComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('person.entity', environment.api + '/reference/person', '/person')

  constructor() {}

  ngOnInit(): void {}

}
