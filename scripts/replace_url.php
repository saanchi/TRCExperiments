<?php

$file = $argv[1]; 
$fh = fopen( $file, 'r');
$line = fgets($fh);
while(!feof($fh)){
    $string = preg_replace('/\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|$!:,.;]*[A-Z0-9+&@#\/%=~_|$]/i', '', $line);
    print_r($string);
    $line = fgets($fh);
}
fclose($fh);

?>
