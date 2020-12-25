![Testing Workflow](https://github.com/pranayairan/CoinBit/workflows/Testing%20Workflow/badge.svg)

# CoinBit
CoinBit is a beautiful CryptoCurrency app, completely open sourced and 100% in kotlin. It supports following features

* Track prices of over 3000+ currencies over 100+ exchanges
* Get top coins, top pairs, top exchanges by volume. 
* Track latest news for all the coins and crypto community in general
* Completely secure, your data never leaves your device. 
* Choose your home currency and track prices in it. 
* Made with ❤️ and help from [open source community](https://github.com/pranayairan/CoinBit/blob/master/attribution.md)
* Open for contribution, please send a pull request. 

App available on Google Play: https://play.google.com/store/apps/details?id=com.binarybricks.coinbit

## Work in progress

* Ability to add transactions
* Ability to change exchanges
* Autorefresh of prices
* Candle charts

# App Architecture

Currently the app is using MVP with a repository. We have 2 data source, Room and in memory cache. Data flow is like this 

Fragments/Activities -> Presenter -> Repo -> Network/Cache (room/in memory)

In coming days I would like to remove inmemory cache and make everything come from Room. Network will keep the Room cache updated. This will give app some offline abilities. 

I am also using a ton of recycler view with [Adapter Delegate Pattern](http://hannesdorfmann.com/android/adapter-delegates). This enables me to plug and play the screens like Lego blocks. I am thinking to replace this with Epoxy in coming days. 


# Screenshots
<a href="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/0.jpg"><img src="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/0.jpg" height="480" width="240" ></a>
<a href="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/1.jpg"><img src="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/1.jpg" height="480" width="240" ></a>
<a href="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/2.jpg"><img src="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/2.jpg" height="480" width="240" ></a>

<a href="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/3.jpg"><img src="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/3.jpg" height="480" width="240" ></a>
<a href="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/4.jpg"><img src="https://raw.githubusercontent.com/pranayairan/CoinBit/master/screenshots/variant_main/4.jpg" height="480" width="240" ></a>
