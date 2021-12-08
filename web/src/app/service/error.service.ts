import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  transportError(status: number) {
    console.error(status)
  }

  logicError(message: string) {
    console.error(message)
  }

}
