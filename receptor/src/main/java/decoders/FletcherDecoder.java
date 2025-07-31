package decoders;

import core.ValidationResult;

public class FletcherDecoder {
    String message;
    int f_type;
    int block_size;
    int mod;
    int sum1;
    int sum2;
    int sum1Recv;
    int sum2Recv;

    public FletcherDecoder(String message, int f_type) {
        this.message = message;
        this.f_type = f_type;
        this.block_size = f_type / 2;
        this.mod = (int) Math.pow(2, f_type / 2) - 1;
        this.sum1 = 0;
        this.sum2 = 0;
        this.sum1Recv = 0;
        this.sum2Recv = 0;
    }

    public ValidationResult DecodeMessage() {
        String dataBits = message.substring(0, message.length() - f_type);
        String checksumBits = message.substring(message.length() - f_type);

        String sum2Bin = checksumBits.substring(0, block_size);
        String sum1Bin = checksumBits.substring(block_size);

        sum1Recv = Integer.parseInt(sum1Bin, 2);
        sum2Recv = Integer.parseInt(sum2Bin, 2);

        for (int i = 0; i < dataBits.length(); i += block_size) {
            String block = dataBits.substring(i, Math.min(i + block_size, dataBits.length()));
            if (block.length() < block_size) {
                block = String.format("%-" + block_size + "s", block).replace(' ', '0');
            }
            
            int value = Integer.parseInt(block, 2);
            sum1 = (sum1 + value) % mod;
            sum2 = (sum2 + sum1) % mod;
        }

        return new ValidationResult(
            validateMessage(), 
            0,
            false, 
            dataBits
        );
    }

    public Boolean validateMessage() {
        return (sum1 != sum1Recv) || (sum2 != sum2Recv);
    }


}
