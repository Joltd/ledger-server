export class Turnover {
  entries: TurnoverEntry[] = []
  total!: TurnoverEntry
}

export class TurnoverEntry {
  code: string = ''
  beforeDt: string = '2 000 000'
  beforeCt: string = '2 000 000'
  turnoverDt: string = '2 000 000'
  turnoverCt: string = '2 000 000'
  afterDt: string = '2 000 000'
  afterCt: string = '2 000 000'

  constructor(code: string) {
    this.code = code;
  }
}
