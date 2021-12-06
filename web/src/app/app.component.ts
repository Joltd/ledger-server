import {Component, ComponentFactoryResolver, OnInit, Type, ViewChild, ViewContainerRef} from '@angular/core';
import {OverlayService} from "../platform/service/overlay.service";
import {MatSidenav} from "@angular/material/sidenav";
import {from, Observable} from "rxjs";
import {map} from "rxjs/operators";
import {OverlayCommand} from "../platform/model/overlay-command";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  @ViewChild('right', { read: MatSidenav })
  right!: MatSidenav

  @ViewChild('rightContainer', { read: ViewContainerRef })
  container!: ViewContainerRef

  constructor(
    private resolver: ComponentFactoryResolver,
    private overlayService: OverlayService,
    private breakpointObserver: BreakpointObserver
  ) {}

  ngOnInit(): void {
    this.overlayService.setupSide(component => this.openRightSide(component), () => this.closeRightSide())
  }

  isHandset(): boolean {
    return this.breakpointObserver.isMatched(Breakpoints.Handset)
  }

  toolbarCommands(): OverlayCommand[] {
    return this.overlayService.toolbarCommands()
  }

  nestedCommands(): OverlayCommand[] {
    return this.overlayService.nestedCommands()
  }

  hasNestedCommands(): boolean {
    return this.overlayService.hasNestedCommands()
  }

  openRightSide<T>(component: Type<T>): Observable<T> {
    this.container.clear()
    let factory = this.resolver.resolveComponentFactory(component);
    let componentRef = this.container.createComponent(factory);
    return from(this.right.open())
      .pipe(map(() => componentRef.instance))
  }

  closeRightSide() {
    this.right.close().then()
  }

}
