export class Entity {
  properties: Property[] = []
}

export class Property {
  reference!: string
  value!: string
  label!: string
}

export class Reference {
  id!: number
  name!: string
}
