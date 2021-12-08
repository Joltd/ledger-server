import {Component, OnInit} from '@angular/core';
import {Reference} from "../../../platform/model/reference";
import {environment} from "../../../environments/environment";
import {Descriptor} from "../../../platform/model/descriptor";

@Component({
  selector: 'account-browser',
  templateUrl: './account-browser.component.html',
  styleUrls: ['./account-browser.component.scss']
})
export class AccountBrowserComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('account.entity', environment.api + '/reference/account', '/account')

  constructor() {}

  ngOnInit(): void {}

}
