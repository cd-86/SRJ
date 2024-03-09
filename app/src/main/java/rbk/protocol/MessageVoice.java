// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message_voice.proto

package rbk.protocol;

public final class MessageVoice {
  private MessageVoice() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface Message_VoiceOrBuilder extends
      // @@protoc_insertion_point(interface_extends:rbk.protocol.Message_Voice)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int32 length = 1;</code>
     * @return The length.
     */
    int getLength();

    /**
     * <code>bytes data = 2;</code>
     * @return The data.
     */
    com.google.protobuf.ByteString getData();
  }
  /**
   * Protobuf type {@code rbk.protocol.Message_Voice}
   */
  public static final class Message_Voice extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:rbk.protocol.Message_Voice)
      Message_VoiceOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Message_Voice.newBuilder() to construct.
    private Message_Voice(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Message_Voice() {
      data_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Message_Voice();
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return rbk.protocol.MessageVoice.internal_static_rbk_protocol_Message_Voice_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return rbk.protocol.MessageVoice.internal_static_rbk_protocol_Message_Voice_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              rbk.protocol.MessageVoice.Message_Voice.class, rbk.protocol.MessageVoice.Message_Voice.Builder.class);
    }

    public static final int LENGTH_FIELD_NUMBER = 1;
    private int length_ = 0;
    /**
     * <code>int32 length = 1;</code>
     * @return The length.
     */
    @java.lang.Override
    public int getLength() {
      return length_;
    }

    public static final int DATA_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes data = 2;</code>
     * @return The data.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getData() {
      return data_;
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
      if (length_ != 0) {
        output.writeInt32(1, length_);
      }
      if (!data_.isEmpty()) {
        output.writeBytes(2, data_);
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (length_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, length_);
      }
      if (!data_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, data_);
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
      if (!(obj instanceof rbk.protocol.MessageVoice.Message_Voice)) {
        return super.equals(obj);
      }
      rbk.protocol.MessageVoice.Message_Voice other = (rbk.protocol.MessageVoice.Message_Voice) obj;

      if (getLength()
          != other.getLength()) return false;
      if (!getData()
          .equals(other.getData())) return false;
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
      hash = (37 * hash) + LENGTH_FIELD_NUMBER;
      hash = (53 * hash) + getLength();
      hash = (37 * hash) + DATA_FIELD_NUMBER;
      hash = (53 * hash) + getData().hashCode();
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static rbk.protocol.MessageVoice.Message_Voice parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static rbk.protocol.MessageVoice.Message_Voice parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static rbk.protocol.MessageVoice.Message_Voice parseFrom(
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
    public static Builder newBuilder(rbk.protocol.MessageVoice.Message_Voice prototype) {
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
     * Protobuf type {@code rbk.protocol.Message_Voice}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:rbk.protocol.Message_Voice)
        rbk.protocol.MessageVoice.Message_VoiceOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return rbk.protocol.MessageVoice.internal_static_rbk_protocol_Message_Voice_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return rbk.protocol.MessageVoice.internal_static_rbk_protocol_Message_Voice_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                rbk.protocol.MessageVoice.Message_Voice.class, rbk.protocol.MessageVoice.Message_Voice.Builder.class);
      }

      // Construct using rbk.protocol.MessageVoice.Message_Voice.newBuilder()
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
        length_ = 0;
        data_ = com.google.protobuf.ByteString.EMPTY;
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return rbk.protocol.MessageVoice.internal_static_rbk_protocol_Message_Voice_descriptor;
      }

      @java.lang.Override
      public rbk.protocol.MessageVoice.Message_Voice getDefaultInstanceForType() {
        return rbk.protocol.MessageVoice.Message_Voice.getDefaultInstance();
      }

      @java.lang.Override
      public rbk.protocol.MessageVoice.Message_Voice build() {
        rbk.protocol.MessageVoice.Message_Voice result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public rbk.protocol.MessageVoice.Message_Voice buildPartial() {
        rbk.protocol.MessageVoice.Message_Voice result = new rbk.protocol.MessageVoice.Message_Voice(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(rbk.protocol.MessageVoice.Message_Voice result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.length_ = length_;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.data_ = data_;
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
        if (other instanceof rbk.protocol.MessageVoice.Message_Voice) {
          return mergeFrom((rbk.protocol.MessageVoice.Message_Voice)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(rbk.protocol.MessageVoice.Message_Voice other) {
        if (other == rbk.protocol.MessageVoice.Message_Voice.getDefaultInstance()) return this;
        if (other.getLength() != 0) {
          setLength(other.getLength());
        }
        if (other.getData() != com.google.protobuf.ByteString.EMPTY) {
          setData(other.getData());
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
                length_ = input.readInt32();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              case 18: {
                data_ = input.readBytes();
                bitField0_ |= 0x00000002;
                break;
              } // case 18
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

      private int length_ ;
      /**
       * <code>int32 length = 1;</code>
       * @return The length.
       */
      @java.lang.Override
      public int getLength() {
        return length_;
      }
      /**
       * <code>int32 length = 1;</code>
       * @param value The length to set.
       * @return This builder for chaining.
       */
      public Builder setLength(int value) {

        length_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>int32 length = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearLength() {
        bitField0_ = (bitField0_ & ~0x00000001);
        length_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes data = 2;</code>
       * @return The data.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString getData() {
        return data_;
      }
      /**
       * <code>bytes data = 2;</code>
       * @param value The data to set.
       * @return This builder for chaining.
       */
      public Builder setData(com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        data_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <code>bytes data = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearData() {
        bitField0_ = (bitField0_ & ~0x00000002);
        data_ = getDefaultInstance().getData();
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


      // @@protoc_insertion_point(builder_scope:rbk.protocol.Message_Voice)
    }

    // @@protoc_insertion_point(class_scope:rbk.protocol.Message_Voice)
    private static final rbk.protocol.MessageVoice.Message_Voice DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new rbk.protocol.MessageVoice.Message_Voice();
    }

    public static rbk.protocol.MessageVoice.Message_Voice getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Message_Voice>
        PARSER = new com.google.protobuf.AbstractParser<Message_Voice>() {
      @java.lang.Override
      public Message_Voice parsePartialFrom(
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

    public static com.google.protobuf.Parser<Message_Voice> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Message_Voice> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public rbk.protocol.MessageVoice.Message_Voice getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rbk_protocol_Message_Voice_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rbk_protocol_Message_Voice_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023message_voice.proto\022\014rbk.protocol\"-\n\rM" +
      "essage_Voice\022\016\n\006length\030\001 \001(\005\022\014\n\004data\030\002 \001" +
      "(\014b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_rbk_protocol_Message_Voice_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_rbk_protocol_Message_Voice_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rbk_protocol_Message_Voice_descriptor,
        new java.lang.String[] { "Length", "Data", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}