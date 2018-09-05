#include <stdio.h>
#include <stdint.h>

// Taken from StackOverflow
void printBits(size_t const size, void const * const ptr)
{
    unsigned char *b = (unsigned char*) ptr;
    unsigned char byte;
    int i, j;

    for (i=size-1;i>=0;i--)
    {
        for (j=7;j>=0;j--)
        {
            byte = (b[i] >> j) & 1;
            printf("%u", byte);
        }
    }
}

void floatToBuffer(float data)
{
	union 
	{
	  float    floatData;
	  uint32_t bitData;
	} pun = { .floatData = data };

	uint32_t mask = 0xF0000000;
	int i;
	char hexVal;
	char hexValArray[8] = {'0'};

	printf("\nConversion:\ndata inp: %f\t", data);
	printBits(sizeof(data), &data);
	printf("\ndata out: %d\t", pun.bitData);
	printBits(sizeof(pun.bitData), &pun.bitData);
	printf("\n\nMasking bits:\n");

	for(i = 7; i > -1; i--)
	{
		uint32_t val = (pun.bitData & mask);
		val >>= (i*4);
		printf("val: %d\t", val);
		printBits(sizeof(val), &val);
		printf("\n");
		mask >>= 4;

		if(val < 0xA)
			hexVal = val + 0x30;
		else if(val > 0x9)
			hexVal = val + 0x37;

		hexValArray[7-i] = hexVal;
	}

	printf("\nResulting hex:\n");
	for(i = 0; i < 8; i++)
		printf("%c", hexValArray[i]);

	printf("\n\n");
}

int main(int argc, char const *argv[])
{
	floatToBuffer(1.2345);

	return 0;
}