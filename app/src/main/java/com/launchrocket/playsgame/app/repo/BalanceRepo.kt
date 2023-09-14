package com.launchrocket.playsgame.app.repo

class BalanceRepo() {
    lateinit var saveCallback: (Int) -> Unit
    lateinit var getCallback: () -> Int
}