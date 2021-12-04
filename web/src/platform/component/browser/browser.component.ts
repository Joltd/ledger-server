import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Descriptor} from "../../model/descriptor";
import {TypeUtils} from "../../../core/type-utils";
import {Reference} from "../../model/reference";
import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

@Component({
  selector: 'browser',
  templateUrl: './browser.component.html',
  styleUrls: ['./browser.component.scss']
})
export class BrowserComponent implements OnInit {

  @Input()
  reference!: Reference

  descriptor!: Descriptor
  count: number = 0
  dataSource: DataSource<any> = new RemoteDateSource()

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadDescriptor()
    this.loadCount()
  }

  private loadDescriptor() {
    this.http.get<Descriptor>(this.reference.api + '/descriptor', TypeUtils.of(Descriptor))
      .subscribe(result => this.descriptor = result)
  }

  private loadCount() {
    this.http.post<number>(this.reference.api + '/count', {})
      .subscribe(result => this.count = result)
  }

  private load() {
    this.http.get(this.reference.api + '/')
      .subscribe(result => {})
  }

  columns(): string[] {
    return this.descriptor.dto.fields.map(entry => entry.reference)
  }

}

export class RemoteDateSource implements DataSource<any> {
  connect(collectionViewer: CollectionViewer): Observable<any[]> {
    return collectionViewer.viewChange
      .pipe(
        map(listRange => {
          console.count(`[${listRange.start}] - [${listRange.end}]`)
          return []
        })
      )
  }

  disconnect(collectionViewer: CollectionViewer): void {
  }

}
