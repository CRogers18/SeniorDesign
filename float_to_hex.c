#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>

char *float_to_hex(float floatValue) {

    int hexNumber = *((int*)&floatValue); // int here: 32-bit float into 32-bit integer

    char *hexArray;
	hexArray = malloc(sizeof(char) * 8);
    
    //Export the HEX equivalent to the Char Array
    sprintf(hexArray, "%x", hexNumber);

    return hexArray;
}

int main(void){

	//Float is 7 decimal places in the C language
	float floatValue = 12.3456789;
	char *hexArray = float_to_hex(floatValue);
	printf("%f converted to HEX is: 0x", floatValue);

	for(int i = 0; i < 8; i++)
	{
		printf("%c", hexArray[i]);
	}
}
