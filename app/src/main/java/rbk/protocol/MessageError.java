// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message_error.proto

package rbk.protocol;

public final class MessageError {
  private MessageError() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface Message_ErrorOrBuilder extends
      // @@protoc_insertion_point(interface_extends:rbk.protocol.Message_Error)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int32 module = 1;</code>
     * @return The module.
     */
    int getModule();

    /**
     * <code>int32 code = 2;</code>
     * @return The code.
     */
    int getCode();
  }
  /**
   * Protobuf type {@code rbk.protocol.Message_Error}
   */
  public static final class Message_Error extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:rbk.protocol.Message_Error)
      Message_ErrorOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Message_Error.newBuilder() to construct.
    private Message_Error(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Message_Error() {
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Message_Error();
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return rbk.protocol.MessageError.internal_static_rbk_protocol_Message_Error_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return rbk.protocol.MessageError.internal_static_rbk_protocol_Message_Error_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              rbk.protocol.MessageError.Message_Error.class, rbk.protocol.MessageError.Message_Error.Builder.class);
    }

    public static final int MODULE_FIELD_NUMBER = 1;
    private int module_ = 0;
    /**
     * <code>int32 module = 1;</code>
     * @return The module.
     */
    @java.lang.Override
    public int getModule() {
      return module_;
    }

    public static final int CODE_FIELD_NUMBER = 2;
    private int code_ = 0;
    /**
     * <code>int32 code = 2;</code>
     * @return The code.
     */
    @java.lang.Override
    public int getCode() {
      return code_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (module_ != 0) {
        output.writeInt32(1, module_);
      }
      if (code_ != 0) {
        output.writeInt32(2, code_);
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (module_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, module_);
      }
      if (code_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, code_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof rbk.protocol.MessageError.Message_Error)) {
        return super.equals(obj);
      }
      rbk.protocol.MessageError.Message_Error other = (rbk.protocol.MessageError.Message_Error) obj;

      if (getModule()
          != other.getModule()) return false;
      if (getCode()
          != other.getCode()) return false;
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + MODULE_FIELD_NUMBER;
      hash = (53 * hash) + getModule();
      hash = (37 * hash) + CODE_FIELD_NUMBER;
      hash = (53 * hash) + getCode();
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static rbk.protocol.MessageError.Message_Error parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static rbk.protocol.MessageError.Message_Error parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static rbk.protocol.MessageError.Message_Error parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static rbk.protocol.MessageError.Message_Error parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static rbk.protocol.MessageError.Message_Error parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static rbk.protocol.MessageError.Message_Error parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static rbk.protocol.MessageError.Message_Error parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static rbk.protocol.MessageError.Message_Error parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static rbk.protocol.MessageError.Message_Error parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static rbk.protocol.MessageError.Message_Error parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static rbk.protocol.MessageError.Message_Error parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static rbk.protocol.MessageError.Message_Error parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(rbk.protocol.MessageError.Message_Error prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code rbk.protocol.Message_Error}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:rbk.protocol.Message_Error)
        rbk.protocol.MessageError.Message_ErrorOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return rbk.protocol.MessageError.internal_static_rbk_protocol_Message_Error_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return rbk.protocol.MessageError.internal_static_rbk_protocol_Message_Error_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                rbk.protocol.MessageError.Message_Error.class, rbk.protocol.MessageError.Message_Error.Builder.class);
      }

      // Construct using rbk.protocol.MessageError.Message_Error.newBuilder()
      private Builder() {

      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);

      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        module_ = 0;
        code_ = 0;
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return rbk.protocol.MessageError.internal_static_rbk_protocol_Message_Error_descriptor;
      }

      @java.lang.Override
      public rbk.protocol.MessageError.Message_Error getDefaultInstanceForType() {
        return rbk.protocol.MessageError.Message_Error.getDefaultInstance();
      }

      @java.lang.Override
      public rbk.protocol.MessageError.Message_Error build() {
        rbk.protocol.MessageError.Message_Error result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public rbk.protocol.MessageError.Message_Error buildPartial() {
        rbk.protocol.MessageError.Message_Error result = new rbk.protocol.MessageError.Message_Error(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(rbk.protocol.MessageError.Message_Error result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.module_ = module_;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.code_ = code_;
        }
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof rbk.protocol.MessageError.Message_Error) {
          return mergeFrom((rbk.protocol.MessageError.Message_Error)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(rbk.protocol.MessageError.Message_Error other) {
        if (other == rbk.protocol.MessageError.Message_Error.getDefaultInstance()) return this;
        if (other.getModule() != 0) {
          setModule(other.getModule());
        }
        if (other.getCode() != 0) {
          setCode(other.getCode());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 8: {
                module_ = input.readInt32();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              case 16: {
                code_ = input.readInt32();
                bitField0_ |= 0x00000002;
                break;
              } // case 16
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private int module_ ;
      /**
       * <code>int32 module = 1;</code>
       * @return The module.
       */
      @java.lang.Override
      public int getModule() {
        return module_;
      }
      /**
       * <code>int32 module = 1;</code>
       * @param value The module to set.
       * @return This builder for chaining.
       */
      public Builder setModule(int value) {

        module_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>int32 module = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearModule() {
        bitField0_ = (bitField0_ & ~0x00000001);
        module_ = 0;
        onChanged();
        return this;
      }

      private int code_ ;
      /**
       * <code>int32 code = 2;</code>
       * @return The code.
       */
      @java.lang.Override
      public int getCode() {
        return code_;
      }
      /**
       * <code>int32 code = 2;</code>
       * @param value The code to set.
       * @return This builder for chaining.
       */
      public Builder setCode(int value) {

        code_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <code>int32 code = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearCode() {
        bitField0_ = (bitField0_ & ~0x00000002);
        code_ = 0;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:rbk.protocol.Message_Error)
    }

    // @@protoc_insertion_point(class_scope:rbk.protocol.Message_Error)
    private static final rbk.protocol.MessageError.Message_Error DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new rbk.protocol.MessageError.Message_Error();
    }

    public static rbk.protocol.MessageError.Message_Error getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Message_Error>
        PARSER = new com.google.protobuf.AbstractParser<Message_Error>() {
      @java.lang.Override
      public Message_Error parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<Message_Error> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Message_Error> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public rbk.protocol.MessageError.Message_Error getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rbk_protocol_Message_Error_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rbk_protocol_Message_Error_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023message_error.proto\022\014rbk.protocol\"-\n\rM" +
      "essage_Error\022\016\n\006module\030\001 \001(\005\022\014\n\004code\030\002 \001" +
      "(\005b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_rbk_protocol_Message_Error_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_rbk_protocol_Message_Error_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rbk_protocol_Message_Error_descriptor,
        new java.lang.String[] { "Module", "Code", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
