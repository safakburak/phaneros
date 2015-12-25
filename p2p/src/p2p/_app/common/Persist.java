package p2p._app.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class Persist {

	public static void save(Object object, String path) {

		try {

			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path));
			outputStream.writeObject(object);
			outputStream.flush();
			outputStream.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public static Object load(String path) {

		Object result = null;
		
		try {

			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
			result = inputStream.readObject();
			inputStream.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return result;
	}
}
