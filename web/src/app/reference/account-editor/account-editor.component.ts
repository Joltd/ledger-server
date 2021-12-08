import {Component, OnInit} from '@angular/core';
import {Reference} from "../../../platform/model/reference";
import {environment} from "../../../environments/environment";
import {Descriptor} from "../../../platform/model/descriptor";

@Component({
  selector: 'account-editor',
  templateUrl: './account-editor.component.html',
  styleUrls: ['./account-editor.component.scss']
})
export class AccountEditorComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('account.entity', environment.api + '/reference/account', '/account')

  constructor() {}

  ngOnInit(): void {}

}