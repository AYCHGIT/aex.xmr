package bisq.core.xmr.jsonrpc;

<<<<<<< Upstream, based on branch 'master' of https://github.com/bisq-network/incubator-bisq-xmr-integration.git
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Collection of utilities for working with JSON.
 * 
 * @author woodser
 */
public class JsonUtils {

	// set up Gson object mapper
	private static final Gson DEFAULT_MAPPER = new Gson();

	/**
	 * Serializes an object to a JSON string.
	 * 
	 * @param obj
	 *            is the object to serialize
	 * @return String is the object serialized to a JSON string
	 */
	public static String serialize(Object obj) {
		return serialize(DEFAULT_MAPPER, obj);
	}

	/**
	 * Serializes an object to a JSON string.
	 * 
	 * @param mapper
	 *            is the jackson object mapper to use
	 * @param obj
	 *            is the object to serialize
	 * @return String is the object serialized to a JSON string
	 */
	public static String serialize(Gson mapper, Object obj) {
		try {
			return mapper.toJson(obj);
		} catch (Exception e) {
			throw new JsonException("Error serializing object", e);
		}
	}

	/**
	 * Deserializes JSON to a specific class.
	 * 
	 * @param json
	 *            is the JSON to deserialize
	 * @param clazz
	 *            specifies the class to deserialize to
	 * @return T is the object deserialized from JSON to the given class
	 */
	public static <T> T deserialize(String json, Class<T> clazz) {
		return DEFAULT_MAPPER.fromJson(json, clazz);
	}

	/**
	 * Deserializes JSON to a specific class.
	 * 
	 * @param mapper
	 *            is the jackson object mapper to use
	 * @param json
	 *            is the JSON to deserialize
	 * @param clazz
	 *            specifies the class to deserialize to
	 * @return T is the object deserialized from JSON to the given class
	 */
	public static <T> T deserialize(Gson mapper, String json, Class<T> clazz) {
		try {
			return mapper.fromJson(json, clazz);
		} catch (Exception e) {
			throw new JsonException("Error deserializing json to class", e);
		}
	}

	/**
	 * Deserializes JSON to a parameterized type.
	 * 
	 * @param json
	 *            is the JSON to deserialize
	 * @param type
	 *            is the parameterized type to deserialize to (e.g. new
	 *            TypeReference<Map<String, Object>>(){})
	 * @return T is the object deserialized from JSON to the given parameterized
	 *         type
	 */
	public static <T> T deserialize(String json, Type type) {
		return deserialize(DEFAULT_MAPPER, json, type);
	}

	/**
	 * Deserializes JSON to a parameterized type.
	 * 
	 * @param mapper
	 *            is the jackson object mapper to use
	 * @param json
	 *            is the JSON to deserialize
	 * @param type
	 *            is the parameterized type to deserialize to (e.g. new
	 *            TypeReference<Map<String, Object>>(){})
	 * @return T is the object deserialized from JSON to the given parameterized
	 *         type
	 */
	public static <T> T deserialize(Gson mapper, String json, Type type) {
		try {
			return mapper.fromJson(json, type);
		} catch (Exception e) {
			throw new JsonException("Error deserializing json to type " + type.getTypeName(), e);
		}
	}

	/**
	 * Converts a JSON string to a map.
	 * 
	 * @param json
	 *            is the string to convert to a map
	 * @return Map<String, Object> is the json string converted to a map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String json) {
		return toMap(DEFAULT_MAPPER, json);
	}

	/**
	 * Converts a JSON string to a map.
	 * 
	 * @param mapper
	 *            is the jackson object mapper to use
	 * @param json
	 *            is the string to convert to a map
	 * @return Map<String, Object> is the json string converted to a map
	 */
	public static Map<String, Object> toMap(Gson mapper, String json) {
		return mapper.fromJson(json, Map.class);
	}

	/**
	 * Converts an object to a map.
	 * 
	 * @param obj
	 *            is the object to a convert to a map
	 * @return Map<String, Object> is the object converted to a map
	 */
	public static Map<String, Object> toMap(Object obj) {
		return toMap(DEFAULT_MAPPER, obj);
	}

	/**
	 * Converts an object to a map.
	 * 
	 * @param mapper
	 *            is the jackson object mapper to use
	 * @param obj
	 *            is the object to a convert to a map
	 * @return Map<String, Object> is the object converted to a map
	 */
	public static Map<String, Object> toMap(Gson mapper, Object obj) {
		return toMap(mapper.toJson(obj));
	}
=======
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Collection of utilities for working with JSON.
 * 
 * @author woodser
 */
public class JsonUtils {
  
  // set up jackson object mapper
  private static final ObjectMapper DEFAULT_MAPPER;
  static {
    DEFAULT_MAPPER = new ObjectMapper();
    DEFAULT_MAPPER.setSerializationInclusion(Include.NON_NULL);
  }
  
  /**
   * Serializes an object to a JSON string.
   * 
   * @param obj is the object to serialize
   * @return String is the object serialized to a JSON string
   */
  public static String serialize(Object obj) {
    return serialize(DEFAULT_MAPPER, obj);
  }
  
  /**
   * Serializes an object to a JSON string.
   * 
   * @param mapper is the jackson object mapper to use
   * @param obj is the object to serialize
   * @return String is the object serialized to a JSON string
   */
  public static String serialize(ObjectMapper mapper, Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new JsonException("Error serializing object", e);
    }
  }
  
  /**
   * Deserializes JSON to a specific class.
   * 
   * @param json is the JSON to deserialize
   * @param clazz specifies the class to deserialize to
   * @return T is the object deserialized from JSON to the given class
   */
  public static <T> T deserialize(String json, Class<T> clazz) {
    return deserialize(DEFAULT_MAPPER, json, clazz);
  }
  
  /**
   * Deserializes JSON to a specific class.
   * 
   * @param mapper is the jackson object mapper to use
   * @param json is the JSON to deserialize
   * @param clazz specifies the class to deserialize to
   * @return T is the object deserialized from JSON to the given class
   */
  public static <T> T deserialize(ObjectMapper mapper, String json, Class<T> clazz) {
    try {
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      throw new JsonException("Error deserializing json to class", e);
    }
  }
  
  /**
   * Deserializes JSON to a parameterized type.
   * 
   * @param json is the JSON to deserialize
   * @param type is the parameterized type to deserialize to (e.g. new TypeReference<Map<String, Object>>(){})
   * @return T is the object deserialized from JSON to the given parameterized type
   */
  public static <T> T deserialize(String json, TypeReference<T> type) {
    return deserialize(DEFAULT_MAPPER, json, type);
  }
  
  /**
   * Deserializes JSON to a parameterized type.
   * 
   * @param mapper is the jackson object mapper to use
   * @param json is the JSON to deserialize
   * @param type is the parameterized type to deserialize to (e.g. new TypeReference<Map<String, Object>>(){})
   * @return T is the object deserialized from JSON to the given parameterized type
   */
  public static <T> T deserialize(ObjectMapper mapper, String json, TypeReference<T> type) {
    try {
      return (T) mapper.readValue(json, type);
    } catch (Exception e) {
      throw new JsonException("Error deserializing json to type " + type.getType(), e);
    }
  }

  /**
   * Converts a JSON string to a map.
   * 
   * @param json is the string to convert to a map
   * @return Map<String, Object> is the json string converted to a map
   */
  public static Map<String, Object> toMap(String json) {
    return deserialize(DEFAULT_MAPPER, json, new TypeReference<Map<String, Object>>(){});
  }
  
  /**
   * Converts a JSON string to a map.
   * 
   * @param mapper is the jackson object mapper to use
   * @param json is the string to convert to a map
   * @return Map<String, Object> is the json string converted to a map
   */
  public static Map<String, Object> toMap(ObjectMapper mapper, String json) {
    return deserialize(mapper, json, new TypeReference<Map<String, Object>>(){});
  }
  
  /**
   * Converts an object to a map.
   * 
   * @param obj is the object to a convert to a map
   * @return Map<String, Object> is the object converted to a map
   */
  public static Map<String, Object> toMap(Object obj) {
    return toMap(DEFAULT_MAPPER, serialize(obj));
  }
  
  /**
   * Converts an object to a map.
   * 
   * @param mapper is the jackson object mapper to use
   * @param obj is the object to a convert to a map
   * @return Map<String, Object> is the object converted to a map
   */
  public static Map<String, Object> toMap(ObjectMapper mapper, Object obj) {
    return toMap(mapper, serialize(obj));
  }
>>>>>>> cf956db Fully functional and basic Monero (XMR) wallet integrated to Monero RPC Wallet running on localhost with the following features:
}
