package codechicken.microblock;

import net.minecraft.item.ItemStack;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\6\1!2q!\1\2\17\2\7\5qAA\2TC^T!a\1\3\2\215L7M]8cY>\287NC\1\6\3-\25w\14Z3dQ&\287.\268\4\1M\17\1\1\3\t\3\19Ai\17A\3\6\3\231\tA!\27;f[*\17QBD\1\n[&tWm\25:bMRT\17aD\1\4]\22$\24BA\t\11\5\17IE/Z7\t\11M\1A\17\1\11\2\r\17Jg.\27;%)\5)\2C\1\f\26\27\59\"\"\1\r\2\11M\28\23\r\\1\n\5i9\"\1B+oSRDQ\1\b\1\5\2u\tQcZ3u\27\6D8)\30;uS:<7\11\30:f]\30$\b.F\1\31!\t1r$\3\2!/\t\25\17J\28;\t\11\t\2a\17A\18\2%\29,GoQ;ui&twm\21;sK:<G\15\27\11\3=\17BQaC\17A\2\21\2\"!\3\20\n\5\29R!!C%uK6\28F/Y2l\1")
public abstract interface Saw {
	public abstract int getMaxCuttingStrength();

	public abstract int getCuttingStrength(ItemStack paramItemStack);
}