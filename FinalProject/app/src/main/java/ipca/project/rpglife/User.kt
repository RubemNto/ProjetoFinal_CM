package ipca.project.rpglife

class User {
    public var UserClass = 1
    public var Name: String = "Name"
    public var XP: Int = 0
    public var TotalSteps: Int = 0
    public var Calories: Float = 0f

    public var NewDay = false

    public lateinit var StartDate : String
    public lateinit var EndDate : String

    constructor(
        userClass: Int,
        name: String,
        xp: Int,
        totalSteps: Int,
        calories: Float,
        startDate:String,
        endDate:String,
    ){
        UserClass = userClass
        Name = name
        XP = xp
        TotalSteps = totalSteps
        Calories = calories
        StartDate = startDate
        EndDate = endDate
    }
}