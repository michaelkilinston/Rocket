package go_to.play_in.rocket.repo

class BalanceRepo() {
    lateinit var saveCallback: (Int) -> Unit
    lateinit var getCallback: () -> Int
}