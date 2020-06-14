package machine

import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)

    val machine = CoffeeMachine(400, 540, 120, 9, 550)
    machine.start()
    while (machine.isWorking()) {
        machine.input(scanner.nextLine())
    }

    scanner.close()
}

class CoffeeMachine(
        private var water: Int,
        private var milk: Int,
        private var beans: Int,
        private var cups: Int,
        private var money: Int
) {
    private var state = State.NOT_WORKING
        set(value) {
            when (value) {
                State.IDLE -> print("\nWrite action (buy, fill, take, remaining, exit): ")
                State.BUY -> print("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")
                State.FILL -> print("\nWrite how many ml of water do you want to add: ")
                State.FILL_MILK -> print("Write how many ml of milk do you want to add: ")
                State.FILL_BEANS -> print("Write how many grams of coffee beans do you want to add: ")
                State.FILL_CUPS -> print("Write how many disposable cups of coffee do you want to add: ")
                State.NOT_WORKING -> Unit
            }
            field = value
        }

    fun start() {
        state = State.IDLE
    }

    fun isWorking() = state != State.NOT_WORKING

    fun input(action: String) {
        state = when (state) {
            State.IDLE -> {
                when (action) {
                    "buy" -> State.BUY
                    "fill" -> State.FILL
                    "take" -> {
                        println()
                        take()
                        State.IDLE
                    }
                    "remaining" -> {
                        showState()
                        State.IDLE
                    }
                    "exit" -> State.NOT_WORKING
                    else -> State.IDLE
                }
            }
            State.BUY -> {
                buy(action)
                State.IDLE
            }
            State.FILL -> {
                water += action.toInt()
                State.FILL_MILK
            }
            State.FILL_MILK -> {
                milk += action.toInt()
                State.FILL_BEANS
            }
            State.FILL_BEANS -> {
                beans += action.toInt()
                State.FILL_CUPS
            }
            State.FILL_CUPS -> {
                cups += action.toInt()
                State.IDLE
            }
            State.NOT_WORKING -> State.NOT_WORKING
        }
    }

    private fun showState() {
        println("""
            
            The coffee machine has:
            $water of water
            $milk of milk
            $beans of coffee beans
            $cups of disposable cups
            $$money of money
        """.trimIndent())
    }

    private fun take() {
        println("I gave you \$$money")
        money = 0
    }

    private fun buy(action: String) {
        when (action) {
            "1" -> makeDrink(Drink.ESPRESSO)
            "2" -> makeDrink(Drink.LATTE)
            "3" -> makeDrink(Drink.CAPPUCCINO)
        }
    }

    private fun makeDrink(drink: Drink) {
        val error = checkIngredients(drink)
        if (error.isEmpty()) {
            water -= drink.water
            milk -= drink.milk
            beans -= drink.beans
            money += drink.money
            cups--
            println("I have enough resources, making you a coffee!")
        } else {
            println(error)
        }
    }

    private fun checkIngredients(drink: Drink): String {
        return when {
            water < drink.water -> "Sorry, not enough water!"
            milk < drink.milk -> "Sorry, not enough milk!"
            beans < drink.beans -> "Sorry, not enough beans!"
            cups < 1 -> "Sorry, not enough cups!"
            else -> ""
        }
    }

    enum class Drink(val water: Int, val milk: Int, val beans: Int, val money: Int) {
        ESPRESSO(250, 0, 16, 4),
        LATTE(350, 75, 20, 7),
        CAPPUCCINO(200, 100, 12, 6),
    }

    enum class State {
        IDLE, BUY, FILL, FILL_MILK, FILL_BEANS, FILL_CUPS, NOT_WORKING,
    }
}