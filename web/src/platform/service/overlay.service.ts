import {Injectable, Type} from '@angular/core';
import {Observable, of} from "rxjs";
import {OverlayCommand} from "../model/overlay-command";

@Injectable({
  providedIn: 'root'
})
export class OverlayService {

  private _openSide: (component: Type<any>) => Observable<any> = () => of()
  private _closeSide: () => void = () => {}
  private commands: OverlayCommand[] = []

  constructor() {}

  setupCommands(commands: OverlayCommand[]) {
    for (let index = 0; index < commands.length; index++){
      let command = commands[index];
      if (index > 2) {
        command.nested = true
      }
    }
    setTimeout(() => this.commands = commands, 1) // ugly hack
  }

  toolbarCommands(): OverlayCommand[] {
    return this.commands.filter(command => !command.nested)
  }

  nestedCommands(): OverlayCommand[] {
    return this.commands.filter(command => command.nested)
  }

  hasNestedCommands(): boolean {
    return this.commands.find(command => command.nested) != undefined
  }

  setupSide(
    openSide: (component: Type<any>) => Observable<any>,
    closeSide: () => void
  ) {
    this._openSide = openSide
    this._closeSide = closeSide
  }

  openSide<T>(component: Type<T>): Observable<T> {
    return this._openSide(component) as Observable<T>
  }

  closeSide() {
    this._closeSide()
  }

}
