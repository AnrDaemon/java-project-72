@param String value
@param int length = 200
@if(value != null && value.length() > length)${value.substring(0, length)}...@else${value}@endif