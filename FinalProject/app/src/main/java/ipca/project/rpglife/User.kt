package ipca.project.rpglife

class User {
    var Name: String = "Name"
    var XP: Float = 0f
    var Steps: Int = 0
    var Calories: Float = 0f

    var Gold: Int = 0
    var Rocks: Int = 0
    var Wood: Int = 0
    var Leather: Int = 0

    constructor(
        name: String,
        xp: Float,
        steps: Int,
        calories: Float,
        gold: Int,
        rocks: Int,
        wood: Int,
        leather: Int
    ){
        Name = name
        XP = xp
        Steps = steps
        Calories = calories
        Gold = gold
        Rocks = rocks
        Wood = wood
        Leather = leather
    }
}