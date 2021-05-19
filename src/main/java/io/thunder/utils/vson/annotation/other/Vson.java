package io.thunder.utils.vson.annotation.other;


import com.google.gson.Gson;
import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.elements.VsonArray;
import io.thunder.utils.vson.elements.VsonLiteral;
import io.thunder.utils.vson.elements.object.VsonObject;
import io.thunder.utils.vson.enums.FileFormat;
import io.thunder.utils.vson.manage.vson.VsonParser;
import io.thunder.utils.vson.tree.VsonTree;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Vson {

	private static Vson instance;
	private final List<VsonAdapter<?>> adapters;
	private final VsonParser parser;

	public Vson() {
		this.adapters = new LinkedList<>();
		this.parser = new VsonParser();
	}

	public static Vson get() {
		if (instance == null) {
			instance = new Vson();
		}
		return instance;
	}

	public VsonParser getParser() {
		return parser;
	}

	public <T> T unparse(VsonValue value, Class<T> tClass) {
		return new Gson().fromJson(value.toString(FileFormat.JSON), tClass);
	}

	public VsonValue parse(Object object) {
		try {
			return this.parser.parse(new Gson().toJsonTree(object).toString());
		} catch (IOException e) {
			return null;
		}
	}

	public void registerAdapter(VsonAdapter<?> transformer) {
		this.adapters.add(transformer);
	}

	public <T> List<VsonAdapter<T>> getAdapters(Class<T> tClass) {
		List<VsonAdapter<T>> list = new LinkedList<>();
		for (VsonAdapter<?> adapter : adapters) {
			if (adapter.getTypeClass().equals(tClass)) {
				list.add((VsonAdapter<T>) adapter);
			}
		}
		return list;
	}

	public VsonValue toVson(Object object) {
		try {
			return createTree(object).toVson();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T> T fromVson(VsonObject vsonObject, Class<T> tClass) {
		try {
			return (T) ImplVsonTree.newTree().from(vsonObject, (Class<Object>) tClass);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T> VsonTree<T> createTree(T object) {
		return ImplVsonTree.newTree(object);
	}

	private static class ImplVsonTree<T> implements VsonTree<T> {

		private final T object;

		private ImplVsonTree() {
			this(null);
		}

		private ImplVsonTree(T object) {
			this.object = object;
		}

		public static <T> ImplVsonTree<T> newTree(T object) {
			return new ImplVsonTree<>(object);
		}

		public static <T> ImplVsonTree<T> newTree() {
			return new ImplVsonTree<>();
		}

		@Override
		public T from(VsonObject vsonObject, Class<T> tClass) throws Exception {
			T object = null;
			for (Constructor<?> declaredConstructor : tClass.getDeclaredConstructors()) {
				Object[] args = new Object[declaredConstructor.getParameters().length];
				for (int i = 0; i < declaredConstructor.getParameterTypes().length; i++) {
					Class<?> parameterType = declaredConstructor.getParameterTypes()[i];
					if (Number.class.isAssignableFrom(parameterType)) {
						args[i] = -1;
					} else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
						args[i] = false;
					} else if (parameterType.equals(int.class) || parameterType.equals(Integer.class)) {
						args[i] = -1;
					} else if (parameterType.equals(double.class) || parameterType.equals(Double.class)) {
						args[i] = -1D;
					} else if (parameterType.equals(short.class) || parameterType.equals(Short.class)) {
						args[i] = (short)-1;
					} else if (parameterType.equals(long.class) || parameterType.equals(Long.class)) {
						args[i] = -1L;
					} else if (parameterType.equals(float.class) || parameterType.equals(Float.class)) {
						args[i] = -1F;
					} else if (parameterType.equals(byte.class) || parameterType.equals(Byte.class)) {
						args[i] = (byte)-1;
					} else {
						args[i] = null;
					}
				}


				object = (T) declaredConstructor.newInstance(args);
			}

			if (object == null) {
				object = tClass.newInstance();
			}


			for (String key : vsonObject.keys()) {
				Field declaredField = object.getClass().getDeclaredField(key);
				declaredField.setAccessible(true);
				Class type = declaredField.getType();

				Object value;
				if (type.equals(UUID.class)) {
					value = UUID.fromString(vsonObject.getString(key));
				} else if (Enum.class.isAssignableFrom(type)) {
					String s  = vsonObject.getString(key);
					String s1 = s.split("\\.")[0];
					Class enumClass = Class.forName(s1);
					Enum en = Enum.valueOf(enumClass, s.split("\\.")[1]);
					declaredField.set(object, en);
					continue;
				} else {

					value = vsonObject.getObject(key);
					if (value instanceof VsonObject) {
						value = new ImplVsonTree<>(object).from((VsonObject) value, type);
					}
				}
				if (value.getClass().equals(VsonLiteral.class)) {

					value = vsonObject.get(key);

					if (value.equals("null") || value.equals(VsonLiteral.NULL)) {
						declaredField.set(object, null);
					}
					if (value.equals(VsonLiteral.TRUE)) {
						declaredField.set(object, true);
					}
					if (value.equals(VsonLiteral.FALSE)) {
						declaredField.set(object, false);
					}

					continue;
				}
				if (type.equals(int.class) && value.getClass().equals(Integer.class)) {
					declaredField.set(object, value);
					continue;
				}
				if (type.equals(double.class) && value.getClass().equals(Double.class)) {
					declaredField.set(object, value);
					continue;
				}
				if (type.equals(short.class) && value.getClass().equals(Short.class)) {
					declaredField.set(object, value);
					continue;
				}
				if (type.equals(long.class) && value.getClass().equals(Long.class)) {
					declaredField.set(object, value);
					continue;
				}
				if (type.equals(byte.class) && value.getClass().equals(Byte.class)) {
					declaredField.set(object, value);
					continue;
				}
				if (type.equals(float.class) && value.getClass().equals(Float.class)) {
					declaredField.set(object, value);
					continue;
				}
				if (type.equals(boolean.class) && value.getClass().equals(Boolean.class)) {
					declaredField.set(object, value);
					continue;
				}
				if (value.getClass().isAssignableFrom(type) || type.isAssignableFrom(value.getClass())) {
					declaredField.set(object, value);
					continue;
				}
				if (value.getClass().equals(Integer.class) && (type.equals(long.class) || type.equals(Long.class))) {
					declaredField.set(object, Long.valueOf("" + value));
					continue;
				}
				if (value.getClass().equals(Integer.class) && (type.equals(Byte.class) || type.equals(byte.class))) {
					declaredField.set(object, Byte.valueOf("" + value));
					continue;
				}
				if (value.getClass().equals(Integer.class) && (type.equals(Double.class) || type.equals(double.class))) {
					declaredField.set(object, Double.valueOf("" + value));
					continue;
				}
				if (value.getClass().equals(Integer.class) && (type.equals(Short.class) || type.equals(short.class))) {
					declaredField.set(object, Short.valueOf("" + value));
					continue;
				}
				if (value.getClass().equals(Integer.class) && (type.equals(Float.class) || type.equals(float.class))) {
					declaredField.set(object, Float.valueOf("" + value));
					continue;
				}
				if (!value.getClass().equals(type)) {
					System.out.println("[ObjectTree] Can't set Field " + key + " of " + tClass.getSimpleName() + " to " + value.getClass().getSimpleName() + " because it requires " + type.getSimpleName() + "!");
					continue;
				}
				declaredField.set(object, value);
			}

			return object;
		}

		@Override
		public VsonValue toVson() throws Exception {
			VsonValue vsonValue = this.fromObject(object);
			if (vsonValue == null) {
				VsonObject vsonObject = new VsonObject();
				for (Field declaredField : object.getClass().getDeclaredFields()) {
					declaredField.setAccessible(true);
					Object o = declaredField.get(this.object);
					VsonValue value = this.fromObject(o);
					if (value == null) {
						if (o.getClass().equals(this.object.getClass())) {
							value = VsonLiteral.NULL;
						} else {
							value = VsonTree.newTree(o).toVson();
						}
					}
					if (value == null) {
						value = VsonLiteral.NULL;
					}
					vsonObject.submit(declaredField.getName(), value);
				}
				return vsonObject;
			}
			return vsonValue;
		}


		private VsonValue fromObject(Object input) throws Exception {
			VsonValue vsonValue = null;
			if (input == null) {
				vsonValue = VsonLiteral.NULL;
			} else if (input instanceof VsonLiteral && input.equals(VsonLiteral.NULL)) {
				vsonValue = VsonValue.valueOf("null");
			} else if (input instanceof UUID || input instanceof String) {
				vsonValue = VsonValue.valueOf(input.toString());
			} else if (input instanceof Double) {
				vsonValue = VsonValue.valueOf((Double) input);
			} else if (input instanceof Enum) {
				Enum<?> en = (Enum<?>) input;
				vsonValue = VsonValue.valueOf(en.getClass().getName() + "." + en.name());
			} else if (input instanceof Long) {
				vsonValue = VsonValue.valueOf((Long) input);
			} else if (input instanceof Byte) {
				vsonValue = VsonValue.valueOf((Byte) input);
			} else if (input instanceof Short) {
				vsonValue = VsonValue.valueOf((Short) input);
			} else if (input instanceof Integer) {
				vsonValue = VsonValue.valueOf((Integer) input);
			} else if (input instanceof Boolean) {
				vsonValue = VsonValue.valueOf((Boolean) input);
			} else if (input instanceof VsonValue) {
				vsonValue = (VsonValue) input;
			} else if (input instanceof Iterable) {
				Iterable<?> it = (Iterable<?>) input;
				VsonArray vsonArray = new VsonArray();
				for (Object o : it) {
					VsonValue vsonValue1 = fromObject(o);
					if (vsonValue1 == null) {
						vsonValue1 = VsonTree.newTree(o).toVson();
					}
					vsonArray.append(vsonValue1);
				}
				vsonValue = vsonArray;
			}
			return vsonValue;
		}
	}
}

