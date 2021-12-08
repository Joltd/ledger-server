import { Component, OnInit } from '@angular/core';
import {Descriptor} from "../../../platform/model/descriptor";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'person-editor',
  templateUrl: './person-editor.component.html',
  styleUrls: ['./person-editor.component.scss']
})
export class PersonEditorComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('person.entity', environment.api + '/reference/person', '/person')

  constructor() {}

  ngOnInit(): void {}

}
