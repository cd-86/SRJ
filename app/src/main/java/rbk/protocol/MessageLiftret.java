// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message_liftret.proto

package rbk.protocol;

public final class MessageLiftret {
  private MessageLiftret() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface Message_Lift_RetOrBuilder extends
      // @@protoc_insertion_point(interface_extends:rbk.protocol.Message_Lift_Ret)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int32 retAction = 1;</code>
     * @return The retAction.
     */
    int getRetAction();

    /**
     * <code>bool toLimitFini = 2;</code>
     * @return The toLimitFini.
     */
    boolean getToLimitFini();

    /**
     * <code>bool spinDegreeFini = 3;</code>
     * @return The spinDegreeFini.
     */
    boolean getSpinDegreeFini();
  }
  /**
   * Protobuf type {@code rbk.protocol.Message_Lift_Ret}
   */
  public static final class Message_Lift_Ret extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:rbk.protocol.Message_Lift_Ret)
      Message_Lift_RetOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Message_Lift_Ret.newBuilder() to construct.
    private Message_Lift_Ret(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Message_Lift_Ret() {
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Message_Lift_Ret();
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return rbk.protocol.MessageLiftret.internal_static_rbk_protocol_Message_Lift_Ret_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return rbk.protocol.MessageLiftret.internal_static_rbk_protocol_Message_Lift_Ret_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              rbk.protocol.MessageLiftret.Message_Lift_Ret.class, rbk.protocol.MessageLiftret.Message_Lift_Ret.Builder.class);
    }

    public static final int RETACTION_FIELD_NUMBER = 1;
    private int retAction_ = 0;
    /**
     * <code>int32 retAction = 1;</code>
     * @return The retAction.
     */
    @java.lang.Override
    public int getRetAction() {
      return retAction_;
    }

    public static final int TOLIMITFINI_FIELD_NUMBER = 2;
    private boolean toLimitFini_ = false;
    /**
     * <code>bool toLimitFini = 2;</code>
     * @return The toLimitFini.
     */
    @java.lang.Override
    public boolean getToLimitFini() {
      return toLimitFini_;
    }

    public static final int SPINDEGREEFINI_FIELD_NUMBER = 3;
    private boolean spinDegreeFini_ = false;
    /**
     * <code>bool spinDegreeFini = 3;</code>
     * @return The spinDegreeFini.
     */
    @java.lang.Override
    public boolean getSpinDegreeFini() {
      return spinDegreeFini_;
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
      if (retAction_ != 0) {
        output.writeInt32(1, retAction_);
      }
      if (toLimitFini_ != false) {
        output.writeBool(2, toLimitFini_);
      }
      if (spinDegreeFini_ != false) {
        output.writeBool(3, spinDegreeFini_);
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (retAction_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, retAction_);
      }
      if (toLimitFini_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(2, toLimitFini_);
      }
      if (spinDegreeFini_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(3, spinDegreeFini_);
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
      if (!(obj instanceof rbk.protocol.MessageLiftret.Message_Lift_Ret)) {
        return super.equals(obj);
      }
      rbk.protocol.MessageLiftret.Message_Lift_Ret other = (rbk.protocol.MessageLiftret.Message_Lift_Ret) obj;

      if (getRetAction()
          != other.getRetAction()) return false;
      if (getToLimitFini()
          != other.getToLimitFini()) return false;
      if (getSpinDegreeFini()
          != other.getSpinDegreeFini()) return false;
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
      hash = (37 * hash) + RETACTION_FIELD_NUMBER;
      hash = (53 * hash) + getRetAction();
      hash = (37 * hash) + TOLIMITFINI_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getToLimitFini());
      hash = (37 * hash) + SPINDEGREEFINI_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getSpinDegreeFini());
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static rbk.protocol.MessageLiftret.Message_Lift_Ret parseFrom(
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
    public static Builder newBuilder(rbk.protocol.MessageLiftret.Message_Lift_Ret prototype) {
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
     * Protobuf type {@code rbk.protocol.Message_Lift_Ret}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:rbk.protocol.Message_Lift_Ret)
        rbk.protocol.MessageLiftret.Message_Lift_RetOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return rbk.protocol.MessageLiftret.internal_static_rbk_protocol_Message_Lift_Ret_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return rbk.protocol.MessageLiftret.internal_static_rbk_protocol_Message_Lift_Ret_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                rbk.protocol.MessageLiftret.Message_Lift_Ret.class, rbk.protocol.MessageLiftret.Message_Lift_Ret.Builder.class);
      }

      // Construct using rbk.protocol.MessageLiftret.Message_Lift_Ret.newBuilder()
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
        retAction_ = 0;
        toLimitFini_ = false;
        spinDegreeFini_ = false;
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return rbk.protocol.MessageLiftret.internal_static_rbk_protocol_Message_Lift_Ret_descriptor;
      }

      @java.lang.Override
      public rbk.protocol.MessageLiftret.Message_Lift_Ret getDefaultInstanceForType() {
        return rbk.protocol.MessageLiftret.Message_Lift_Ret.getDefaultInstance();
      }

      @java.lang.Override
      public rbk.protocol.MessageLiftret.Message_Lift_Ret build() {
        rbk.protocol.MessageLiftret.Message_Lift_Ret result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public rbk.protocol.MessageLiftret.Message_Lift_Ret buildPartial() {
        rbk.protocol.MessageLiftret.Message_Lift_Ret result = new rbk.protocol.MessageLiftret.Message_Lift_Ret(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(rbk.protocol.MessageLiftret.Message_Lift_Ret result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.retAction_ = retAction_;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.toLimitFini_ = toLimitFini_;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.spinDegreeFini_ = spinDegreeFini_;
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
        if (other instanceof rbk.protocol.MessageLiftret.Message_Lift_Ret) {
          return mergeFrom((rbk.protocol.MessageLiftret.Message_Lift_Ret)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(rbk.protocol.MessageLiftret.Message_Lift_Ret other) {
        if (other == rbk.protocol.MessageLiftret.Message_Lift_Ret.getDefaultInstance()) return this;
        if (other.getRetAction() != 0) {
          setRetAction(other.getRetAction());
        }
        if (other.getToLimitFini() != false) {
          setToLimitFini(other.getToLimitFini());
        }
        if (other.getSpinDegreeFini() != false) {
          setSpinDegreeFini(other.getSpinDegreeFini());
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
                retAction_ = input.readInt32();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              case 16: {
                toLimitFini_ = input.readBool();
                bitField0_ |= 0x00000002;
                break;
              } // case 16
              case 24: {
                spinDegreeFini_ = input.readBool();
                bitField0_ |= 0x00000004;
                break;
              } // case 24
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

      private int retAction_ ;
      /**
       * <code>int32 retAction = 1;</code>
       * @return The retAction.
       */
      @java.lang.Override
      public int getRetAction() {
        return retAction_;
      }
      /**
       * <code>int32 retAction = 1;</code>
       * @param value The retAction to set.
       * @return This builder for chaining.
       */
      public Builder setRetAction(int value) {

        retAction_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>int32 retAction = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearRetAction() {
        bitField0_ = (bitField0_ & ~0x00000001);
        retAction_ = 0;
        onChanged();
        return this;
      }

      private boolean toLimitFini_ ;
      /**
       * <code>bool toLimitFini = 2;</code>
       * @return The toLimitFini.
       */
      @java.lang.Override
      public boolean getToLimitFini() {
        return toLimitFini_;
      }
      /**
       * <code>bool toLimitFini = 2;</code>
       * @param value The toLimitFini to set.
       * @return This builder for chaining.
       */
      public Builder setToLimitFini(boolean value) {

        toLimitFini_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <code>bool toLimitFini = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearToLimitFini() {
        bitField0_ = (bitField0_ & ~0x00000002);
        toLimitFini_ = false;
        onChanged();
        return this;
      }

      private boolean spinDegreeFini_ ;
      /**
       * <code>bool spinDegreeFini = 3;</code>
       * @return The spinDegreeFini.
       */
      @java.lang.Override
      public boolean getSpinDegreeFini() {
        return spinDegreeFini_;
      }
      /**
       * <code>bool spinDegreeFini = 3;</code>
       * @param value The spinDegreeFini to set.
       * @return This builder for chaining.
       */
      public Builder setSpinDegreeFini(boolean value) {

        spinDegreeFini_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      /**
       * <code>bool spinDegreeFini = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearSpinDegreeFini() {
        bitField0_ = (bitField0_ & ~0x00000004);
        spinDegreeFini_ = false;
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


      // @@protoc_insertion_point(builder_scope:rbk.protocol.Message_Lift_Ret)
    }

    // @@protoc_insertion_point(class_scope:rbk.protocol.Message_Lift_Ret)
    private static final rbk.protocol.MessageLiftret.Message_Lift_Ret DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new rbk.protocol.MessageLiftret.Message_Lift_Ret();
    }

    public static rbk.protocol.MessageLiftret.Message_Lift_Ret getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Message_Lift_Ret>
        PARSER = new com.google.protobuf.AbstractParser<Message_Lift_Ret>() {
      @java.lang.Override
      public Message_Lift_Ret parsePartialFrom(
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

    public static com.google.protobuf.Parser<Message_Lift_Ret> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Message_Lift_Ret> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public rbk.protocol.MessageLiftret.Message_Lift_Ret getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rbk_protocol_Message_Lift_Ret_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rbk_protocol_Message_Lift_Ret_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\025message_liftret.proto\022\014rbk.protocol\"R\n" +
      "\020Message_Lift_Ret\022\021\n\tretAction\030\001 \001(\005\022\023\n\013" +
      "toLimitFini\030\002 \001(\010\022\026\n\016spinDegreeFini\030\003 \001(" +
      "\010b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_rbk_protocol_Message_Lift_Ret_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_rbk_protocol_Message_Lift_Ret_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rbk_protocol_Message_Lift_Ret_descriptor,
        new java.lang.String[] { "RetAction", "ToLimitFini", "SpinDegreeFini", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}