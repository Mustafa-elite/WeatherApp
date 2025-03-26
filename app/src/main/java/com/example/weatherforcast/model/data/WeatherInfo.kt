package com.example.weatherforcast.model.data

class WeatherInfo(

    var weatherDescription:String="-",
    var windSpeed:Double=0.0,
    var temp:Double=0.0,
    var feelsLike:Double=0.0,
    var minTemp:Double=0.0,
    var maxTemp:Double=0.0,
    var pressure: Int=0,
    var humidityPercentage: Int=0,
    var visibility: Int=0,
    var cloudyPercentage:Int=0,
    var calculationTime:Long=0,
    var timezone: Int=0,
    var sunrise: Long=0,
    var sunset: Long=0,
    var countryName:String="-",
    var cityName:String="-",
    var iconInfo:Int=0,
    var temperatureUnit: TemperatureUnit=TemperatureUnit.KELVIN,
    var windSpeedUnit: WindSpeedUnit=WindSpeedUnit.METER_SECOND
){
    fun convertTemperatureUnit(newTemperatureUnit: TemperatureUnit){
        if(newTemperatureUnit!=this.temperatureUnit)
        {
            temp=convertTemperature(temp,this.temperatureUnit,newTemperatureUnit)
            feelsLike=convertTemperature(feelsLike,this.temperatureUnit,newTemperatureUnit)
            minTemp=convertTemperature(minTemp,this.temperatureUnit,newTemperatureUnit)
            maxTemp=convertTemperature(maxTemp,this.temperatureUnit,newTemperatureUnit)
            this.temperatureUnit=newTemperatureUnit
        }
    }



    private fun convertTemperature(value: Double, from: TemperatureUnit, to: TemperatureUnit): Double {
        val convertedValue = when {
            from == TemperatureUnit.CELSIUS && to == TemperatureUnit.FAHRENHEIT -> value * 9/5 + 32
            from == TemperatureUnit.CELSIUS && to == TemperatureUnit.KELVIN -> value + 273.15
            from == TemperatureUnit.FAHRENHEIT && to == TemperatureUnit.CELSIUS -> (value - 32) * 5/9
            from == TemperatureUnit.FAHRENHEIT && to == TemperatureUnit.KELVIN -> (value - 32) * 5/9 + 273.15
            from == TemperatureUnit.KELVIN && to == TemperatureUnit.CELSIUS -> value - 273.15
            from == TemperatureUnit.KELVIN && to == TemperatureUnit.FAHRENHEIT -> (value - 273.15) * 9/5 + 32
            else -> value
        }
        return (Math.round(convertedValue * 10) / 10.0)
    }

    fun convertWindSpeedUnit(newWindSpeedUnit: WindSpeedUnit){
        if(newWindSpeedUnit!=windSpeedUnit)
        {
            windSpeed=convertWindSpeed(windSpeed,windSpeedUnit,newWindSpeedUnit)
            windSpeedUnit=newWindSpeedUnit
        }
    }
    private fun convertWindSpeed(value: Double, from: WindSpeedUnit, to: WindSpeedUnit): Double {
        val convertedValue = when {
            from == WindSpeedUnit.METER_SECOND && to == WindSpeedUnit.KILOMETER_HOUR -> value * 3.6
            from == WindSpeedUnit.METER_SECOND && to == WindSpeedUnit.MILES_HOUR -> value * 2.237
            from == WindSpeedUnit.KILOMETER_HOUR && to == WindSpeedUnit.METER_SECOND -> value / 3.6
            from == WindSpeedUnit.KILOMETER_HOUR && to == WindSpeedUnit.MILES_HOUR -> value / 1.609
            from == WindSpeedUnit.MILES_HOUR && to == WindSpeedUnit.METER_SECOND -> value / 2.237
            from == WindSpeedUnit.MILES_HOUR && to == WindSpeedUnit.KILOMETER_HOUR -> value * 1.609
            else -> value
        }
        return (Math.round(convertedValue * 10) / 10.0)
    }
}
enum class TemperatureUnit(val unitSymbol: String){
    KELVIN("K"),
    CELSIUS("C"),
    FAHRENHEIT("F")
}
enum class WindSpeedUnit(val unitSymbol: String){
    METER_SECOND("m/s"),
    MILES_HOUR("mph"),
    KILOMETER_HOUR("km/h")
}

